'''
Process org file into network of topic nodes
Hierarchy of text:
Node(s) -> Document(s) -> Collection(s)
'''
import copy
import hashlib
import os
import random
import sys

from nltk.corpus import stopwords
from nltk.corpus import wordnet
import nltk
import numpy as np
import pandas as pd

IDLENGTH = 6
STOPWORDS = set(stopwords.words('english'))


def check_word(word):
    if not wordnet.synsets(word):
        return False
    else:
        return True

def get_word_pos(words):

    word_dict = {}

    for w in words:
        if check_word(w):
            word, pos = nltk.pos_tag([w])[0]
            if pos not in word_dict.keys():
                word_dict[pos] = [word]
            else:
                word_dict[pos].append(word)

    for k,v in word_dict.items():
        word_dict[k] = sorted(list(set(v)))

    return word_dict


class Node:
    '''
    Baseclass for a Node object
    '''
    def __init__(self, index, text='', ID=''):
        self.parents = []
        self.children = []
        self.ID = ID
        self.words = text.split(' ')
        self.text = ' '.join(self.words[1:])
        self.level = len(self.words[0])
        # Remove asterisk for text rep
        self.words = self.words[1:]

        self.docind = index
        self.topics = []
        self.num_topics = 0

        if not ID:
            self.__genhash__()

        self.extract_topics()

    def add_parent(self, parent_node):
        self.parents.append(parent_node.ID)

    def add_child(self, child_node):
        self.children.append(child_node.ID)

    def __str__(self):
        return self.text

    def __repr__(self):
        return 'Node:%s' % self.ID

    def __genhash__(self):
        seed = self.text.encode()
        hash_id = hashlib.sha224(seed)
        hash_id = str(hash_id.hexdigest())

        if IDLENGTH > len(hash_id):
            hash_id = hash_id
        else:
            hash_id = hash_id[:IDLENGTH]

        self.ID = hash_id

    def extract_topics(self):
        '''
        Extract "Topics" from words
        * Topics being nonstop words
        * Topics usually being nouns
        Returns
        -------
        None
        * Set `self.topics`
        * Set `self.num_topics`
        '''
        topics = []
        replace_chars = [',', ';', '"', "'"]
        for w in self.words:
            w = ''.join([c for c in w if c not in replace_chars])
            if w not in STOPWORDS:
                w = ''.join([c for c in w if c != ' '])
                topics.append(w)

        if topics:
            tags = get_word_pos(topics)
            if 'NN' in tags.keys():
                self.topics = tags['NN']
                self.num_topics = len(self.topics)

class Document:
    '''
    Baseclass for a Document object
    '''
    def __init__(self, text='', file_path='', ID='0' * IDLENGTH):
        self.file_path = file_path
        self.nodes = []
        self.level_counts = {}
        self.docstats = {}
        self.topics = []
        self.num_topics = len(self.topics)
        self.text = text
        self.ID = ID

    def __text__(self):
        self.text = '\n'.join(['*' * x.level +
                                ' ' + x.text
                                for x in self.nodes])

    def __str__(self):
        if not self.text:
            self.__text__()
        return self.text

    def __repr__(self):
        return 'Doc:%s' % self.ID

    def __genhash__(self):
        if not self.text:
            self.__text__()
        seed = self.text.encode()
        hash_id = hashlib.sha224(seed)
        hash_id = str(hash_id.hexdigest())

        if IDLENGTH > len(hash_id):
            hash_id = hash_id
        else:
            hash_id = hash_id[:IDLENGTH]

        self.ID = hash_id

    def process_org(self, org_file):

        if not org_file and not self.file_path:
            return ''

        if not self.file_path:
            self.file_path = org_file

        with open(self.file_path, 'r') as f:
            data = f.read()

        separator = '*'
        lines = data.split('\n')
        topic_index = []

        ind = 0
        for l in lines:
            if not isinstance(l, str):
                l = str(l)
            if l:
                if l[0] == '*':
                    topic_index.append(ind)
            ind += 1

        topic_nodes = []

        ind = list(range(0, len(topic_index)))
        for i in ind:
            indx = topic_index[i]
            idea = []
            if i == ind[-1]:
                search_range = range(indx, len(lines))
            else:
                search_range = range(indx, topic_index[(i+1)])

            for _ in search_range:
                idea.append(lines[_])

            idea = ''.join(idea)
            idea = idea.replace('   ', ' ').replace('  ', ' ')
            idea_node = Node(index=i, text=idea)
            topic_nodes.append(idea_node)

        # we have ordered list of ideas
        # now we need to establish hierarchy
        # routine to link parent and child ideas
        nodes = []
        level = 1
        doc_tag = ''.join(['0' for x in range(0, IDLENGTH)])
        reverse = topic_nodes.copy()
        reverse.reverse()

        for i in range(0, len(topic_nodes)):

            node = copy.deepcopy(topic_nodes[i])

            parents = []

            words = node.words
            new_level = node.level

            if new_level == 1:
                node.parents.append(doc_tag)
            else:
                offset = len(reverse) - i
                current_level = new_level
                for parent in reverse[offset:]:
                    parent_level = parent.level
                    if parent_level == 1:
                        node.parents.append(parent.ID)
                        break
                    elif parent_level < current_level:
                        current_level = parent_level
                        node.parents.append(parent.ID)
                    else:
                        continue

            level = new_level
            nodes.append(node)


        self.nodes = nodes
        self.__genhash__()

        topics = []

        for n in self.nodes:
            t = n.topics
            for _t in t:
                if _t not in topics:
                    topics.append(_t)

        if topics:
            self.num_topics = len(topics)
            self.topics = topics

        if self.nodes:
            self.calc_docstats()

    def calc_docstats(self):
        '''
        Get stats about document node connections
        '''
        if not self.nodes:
            return

        series = pd.Series([x.level for x in self.nodes])
        counts = series.value_counts()

        level_index = counts.index
        counts.index = ['Level: %s' % x for x in level_index]

        self.level_counts = counts.to_dict()
        self.docstats['level_counts'] = counts

        # topic stats
        node_num_topics = [x.num_topics for x in self.nodes]
        self.avg_num_topics = np.mean(node_num_topics)
        self.topic_stddev = np.std(node_num_topics)

class Collection:
    '''
    Baseclass for a Collection object to manage collections of `Document` objects
    '''
    def __init__(self):
        self.similarity_matrix = None

    def process_dir(self, d):
        '''
        Process directory of text files
        Parameters
        -------
        d: str
            - Directory path with (.org) text files
        Returns
        -------
        None
            - Set `self.documents` to list of `Documents`
            - Set `self.similarity_matrix` with adjacency matrix of `topics`
        '''
        docs = [d + x for x in os.listdir(d)]
        successes = 0
        ind = list(range(0, len(docs)))
        self.documents = [Document() for _ in ind]

        for i in ind:
            try:
                org_file = docs[i]
                self.documents[i].process_org(org_file)
                successes +=1
            except Exception as e:
                print(e)
                continue


        print("NumFiles: %s" % len(docs))
        print("NumSuccess: %s" % successes)
        print("NumFails: %s" % (len(docs) - successes))

        """
        m = {}
        ind = list(range(0, len(self.documents)))
        for i in ind:
            x = self.documents[i]
            divisor = x.num_topics

            if divisor == 0:
                continue

            cross_vector = {}

            for p in self.documents:
                if p.num_topics == 0:
                    continue
                num_match = float(len([t for t in x.topics if t in p.topics]))
                per_match = num_match / x.num_topics
                cross_vector[p.ID] = per_match

            m[x.ID] = cross_vector

        df = pd.DataFrame(m)
        df = df.reindex(sorted(df.columns), axis=1)

        self.similarity_matrix = df
        """


if __name__ == '__main__':

    if len(sys.argv) < 2:
        print('Usage: python knowledgegraph.py path/to/org/files/')
        sys.exit(0)
    else:
        org_dir = sys.argv[1]

    if os.path.exists(org_dir) and org_dir.endswith('/'):
        knowledgegraph = Collection()

        knowledgegraph.process_dir(org_dir)
        num_docs = len(knowledgegraph.documents)

        while True:
            os.system('clear')

            doc_ind = random.randrange(num_docs)
            doc = knowledgegraph.documents[doc_ind]
            date = doc.file_path.split('/')[-1].split('.')[0]
            node_ind = random.randrange(len(doc.nodes))
            text = doc.nodes[node_ind].text

            print(date + "\n" + text)

            inp = input('>')

            if inp == 'q':
                break
            else:
                continue
    else:
        print('.org dir not valid')
        print('should be in the form "path/to/org/files/" with ending backslash')
        sys.exit(0)
