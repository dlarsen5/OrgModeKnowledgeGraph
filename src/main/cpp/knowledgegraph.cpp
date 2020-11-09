#include "../include/knowledgegraph.hpp"


int main()
{
  std::string orgDir = "/home/neutron/Rap/lyrics/orglyrics/";

  knowledgegraph::Collection collection = knowledgegraph::Collection(orgDir);

  collection.processDir();

  return 0;
}
