#include "../include/knowledgegraph.hpp"

knowledgegraph::Collection::Collection()
{

}

knowledgegraph::Collection::Collection(std::string orgPath)
{
  this->orgPath = orgPath;
}

std::vector<std::string>* knowledgegraph::Collection::getFiles()
{
  DIR* dirp = opendir(this->orgPath.c_str());
  if (!dirp) std::cout << "invalid dir: " << this->orgPath;

  std::vector<std::string> *fileList = new std::vector<std::string>;
  dirent *ent = readdir(dirp);

  while (ent!= NULL) {
    std::string s(ent->d_name);
    s.insert(0, this->orgPath);
    fileList->push_back(s);
    ent = readdir(dirp);
  }

  return fileList;
}

void knowledgegraph::Collection::processDir()
{

  std::vector<std::string> fileList = (*this->getFiles());

  int numDocs = fileList.size();
  int failures = 0;

  for (std::vector<std::string>::iterator it = fileList.begin(); it != fileList.end(); ++it) {
    knowledgegraph::Document doc;
    doc.processOrg((*it));
    if (doc.nodes.empty()) {
      failures += 1;
    } else {
      this->documents.push_back(doc);
    }
  };

  std::cout << "Files: " << numDocs << std::endl;
  std::cout << "Successes: " << (numDocs - failures) << std::endl;
  std::cout << "Failures: " << failures << std::endl;

}

knowledgegraph::Collection::~Collection()
{

}
