#include "../include/knowledgegraph.hpp"

knowledgegraph::Document::Document() {}

knowledgegraph::Document::Document(std::string fileObj)
{
  this->fileObj = fileObj;
}

void knowledgegraph::Document::processOrg(std::string file)
{
  std::string fileContents = getFileContents(file);
  if (fileContents.empty())
    return;
  std::stringstream iss(fileContents);
  buildNodes(iss);
  setId();
  buildEdges();
}

void knowledgegraph::Document::buildNodes(std::stringstream& iss)
{
  std::vector<std::string> lines;
  std::vector<int> topicIndex;
  int index = 0;

  while (iss.good())
    {
      std::string subString;
      std::getline(iss, subString, '\n');
      lines.push_back(subString);
      if (subString.front() == '*')
        topicIndex.push_back(index);
      index += 1;
    }

  // Associate text lines with respective topic lines
  std::string docText = "";
  for (auto it = topicIndex.begin(); it !=topicIndex.end(); ++it) {

    int topicInd = (*it);
    lines.at(topicInd);
    int searchEnd;
    std::string idea;

    if ((it+1) == topicIndex.end()) {
      searchEnd = lines.size();
    } else {
      searchEnd = *(it+1);
    }

    for (int i=topicInd; i<searchEnd; ++i) {
      idea += lines[i];
    }


    std::string cleanIdea;
    replaceNSpaces(idea, cleanIdea);
    docText += cleanIdea;

    knowledgegraph::Node node(cleanIdea);
    this->nodes.push_back(node);
  }
  setText(docText);
}

void knowledgegraph::Document::buildEdges()
{
  setNodeParents();
  //setNodeChildren();
}

void knowledgegraph::Document::setNodeParents()
{
  int currentLevel = 1;

  for (auto it = this->nodes.begin(); it != this->nodes.end(); ++it) {
    int nodeLevel = it->getLevel();
    it->parents.push_back(this->Id);

    if (nodeLevel == 1) {

      continue;

    } else if (nodeLevel == currentLevel) {

      it->parents = (it-1)->parents;

    } else {

      int traverseLevel = nodeLevel;

      for (auto reverse = it; reverse != this->nodes.begin(); --reverse) {

        int parentLevel = (reverse-1)->getLevel();

        if (parentLevel == 1) {

          it->parents.push_back((reverse-1)->getId());

        } else if (parentLevel == traverseLevel) {

          continue;

        } else if (parentLevel > traverseLevel) {

          continue;

        } else {

          traverseLevel = parentLevel;
          it->parents.push_back((reverse-1)->getId());

        }
      }
      currentLevel = nodeLevel;
    }
  }
}

void knowledgegraph::Document::setNodeChildren()
{
  // TODO more efficient way, reverse traverse text
  int currentLevel = 1;

  for (auto it = this->nodes.end()+1; it != this->nodes.begin(); ++it) {
    int nodeLevel = it->getLevel();

    if (nodeLevel == 1) {

      continue;

    } else if (nodeLevel == currentLevel) {

      it->parents = (it-1)->parents;

    } else {

      int traverseLevel = nodeLevel;

      for (auto reverse = it; reverse != this->nodes.begin(); --reverse) {

        int parentLevel = (reverse-1)->getLevel();

        if (parentLevel == 1) {

          it->parents.push_back((reverse-1)->getId());

        } else if (parentLevel == traverseLevel) {

          continue;

        } else if (parentLevel > traverseLevel) {

          continue;

        } else {

          traverseLevel = parentLevel;
          it->parents.push_back((reverse-1)->getId());

        }
      }
      currentLevel = nodeLevel;
    }
  }
}

void knowledgegraph::Document::replaceNSpaces(std::string& unclean, std::string& clean)
{
  int prev_c = 0;
  for (auto it = unclean.begin(); it != unclean.end(); ++it)
    {
      int current_c = (int)(*it);
      if (std::isspace(prev_c) and std::isspace(current_c)) {
        continue;
      }
      clean += (*it);
      prev_c = current_c;
    }
}

std::string knowledgegraph::Document::getFileContents(std::string file)
{
  std::string fileContents;
  std::ifstream ifs;
  ifs.open(file, std::ifstream::in);
  char c = ifs.get();
  while (ifs.good()) {
    fileContents += c;
    c = ifs.get();
  }
  ifs.close();
  return fileContents;
}

void knowledgegraph::Document::setText(std::string& docText)
{
  this->text = docText;
}

std::string knowledgegraph::Document::getText()
{
  return this->text;
}

void knowledgegraph::Document::setId()
{
  this->Id = (int)std::hash<std::string>{}(this->text);
}

knowledgegraph::Document::~Document()
{

}
