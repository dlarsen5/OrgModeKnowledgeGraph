# OrgKnowledgeGraph

Decompose [Emacs Org](https://orgmode.org/) documents into hierarchical objects for NLP processing

Example:

"MyDocument.org":
* my heading
** my subheading
** my other subheading
** a smaller subheading

Translates to an object with the properties:

Document:
-- title: MyDocument.org
-- nodes:
--> Node 1
--- text: "my heading"
--- parents: MyDocument.org
--- children: Node 2, Node 3
--> Node 2
--- text: "my subheading"
--- parents: Node 1
--- children: None
--> Node 3
--- text: "my other subheading"
--- parents: Node 1
--- children: Node 4
--> Node 4
--- text: "a smaller subheading"
--- parents: Node 3
--- children: Node 4

# Python
Python example is easiest to run, just run `python src/main/python/knowledgegraph.py /path/to/org/files/`.

The script will parse the org files and return a prompt going through random lines from each file as a visual example.

Customization could be done by using the `Collection` object inside of `if __name__=='__main__'` to iterate through documents/nodes using `Collection.documents: List[Document]` and `Collection.documents[*].nodes: List[Node]`.
