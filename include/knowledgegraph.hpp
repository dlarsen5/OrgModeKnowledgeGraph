#include <dirent.h>
#include <ctype.h>
#include <cstring>
#include <fstream>
#include <functional>
#include <iostream>
#include <string>
#include <sstream>
#include <stdio.h>
#include <sys/types.h>
#include <streambuf>
#include <vector>

namespace knowledgegraph {
  //int IDLENGTH;
  class Node
  {
    public:
      std::vector<int> children;
      std::vector<int> parents;
      std::string text;

      Node ();
      Node (std::string text);
      ~Node();

      void addParent(int parentId);
      void addChild(int childId);
      int getId();
      int getLevel();

      std::ostream& operator<<(std::ostream& out);

    private:
      int Id;
      int Level;

      void setLevel();
      void setId();
  };

  class Document
  {
    public:
      int level;
      std::vector<Node> nodes;

      Document();
      Document(std::string fileObj);
      ~Document();

      std::string getText();
      int getId();
      void processOrg(std::string file);

    private:
      int Id;
      std::string fileObj;
      std::string text;

      void setId();
      void setText(std::string& docText);
      void buildEdges();
      void setNodeParents();
      void setNodeChildren();
      void buildNodes(std::stringstream& iss);
      void replaceNSpaces(std::string& unclean, std::string& clean);
      std::string getFileContents(std::string file);
  };

  class Collection
  {
    public:
      std::vector<Document> documents;

      Collection();
      Collection(std::string orgPath);
      ~Collection();

      void processDir();

    private:
      std::string orgPath;
      std::vector<std::string>* getFiles();
  };
}
