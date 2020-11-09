#include "../include/knowledgegraph.hpp"

#include <iostream>


int main(int argc, char *argv[])
{
  std::string orgDir = "";
  if (argc > 1) {
    orgDir = argv[1];
  } else {
    std::cout << "Need to add /path/to/org/files/ argument";
    return 0;
  }

  knowledgegraph::Collection collection = knowledgegraph::Collection(orgDir);

  collection.processDir();

  return 0;
}
