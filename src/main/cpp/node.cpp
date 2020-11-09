#include "../include/knowledgegraph.hpp"


knowledgegraph::Node::Node()
{
  this->text = "";
}

knowledgegraph::Node::Node(std::string text)
{
  this->text = text;
  setLevel();
  this->Id = (int)std::hash<std::string>{}(this->text);
}

void knowledgegraph::Node::addParent(int parentId)
{
  this->children.push_back(parentId);
}

void knowledgegraph::Node::addChild(int childId)
{
  this->parents.push_back(childId);
}

int knowledgegraph::Node::getId()
{
  return this->Id;
}

int knowledgegraph::Node::getLevel()
{
  return this->Level;
}

void knowledgegraph::Node::setLevel()
{
  int Level = 0;

  for (auto it = this->text.begin(); it != this->text.end(); ++it) {
    char c = (*it);
    if (c == ' ') {
      break;
    } else {
      Level += 1;
    }
  }
  this->Level = Level;
  this->text = this->text.erase(0, (1+Level));
}

std::ostream& knowledgegraph::Node::operator<<(std::ostream& out)
{
  out << this->Id;
  return out;
}

knowledgegraph::Node::~Node()
{

}
