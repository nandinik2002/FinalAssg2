/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */

import java.util.ArrayList;

 public class AutoComplete implements AutoCompleteInterface {

  private DLBNode root = new DLBNode(' '); //root of the DLB Trie
  private StringBuilder currentPrefix = new StringBuilder(); //running prefix
  private DLBNode currentNode = root; //current DLBNode
  //TODO: Add more instance variables as needed



  @Override
  public boolean add(String word) {
    if(word == null){ //checks to see if the word is null or empty before moving along
      throw new IllegalArgumentException("calls add() with a null word");
    } if(word.length()==0){
      throw new IllegalArgumentException("calls add() with an empty word");
    } else return addNewWord(root, word, 0) != null;
    }


    public DLBNode addNewWord(DLBNode node, String word, int wordIndex) {
    if (wordIndex == word.length()) {
        if (!node.isWord) { //if the nose isnt already a word in the dictionary
            node.isWord = true; //we mark the node as a word in the dictionary first because we are about to add it in
            node.size = 1;
            DLBNode anc = node.parent;
            while (anc != null) { //we move up all the nodes so that it makes space for a new word
                anc.size++;
                anc = anc.parent;
            }
        }
        return node;
    }
    DLBNode child = node.child;
    DLBNode prevChild = null;

    while (node.child != null) {
        if (child.data == word.charAt(wordIndex)) { //if the character matches then we recursively call this method for the next character in the word
            return addNewWord(child, word, wordIndex + 1);
        }
        prevChild = child;
        child = child.nextSibling; //move to the next sibling
    }

    DLBNode newNode = new DLBNode(word.charAt(wordIndex));
    newNode.parent = node;
    if (prevChild == null) { //if there arent any children then the new node becomes the first child of the current node
        node.child = newNode;
    } else {
        prevChild.nextSibling = newNode; //add as next sibling if there are children
        newNode.previousSibling = prevChild; //go back to previous child
    }
    return addNewWord(newNode, word, wordIndex + 1); //recursive call for rest of the characters of the word
}






  @Override
  public boolean advance(char c) {
    DLBNode tempVar = currentNode.child; //created a temp variable just so it makes reading code easier
    if(c == '\0'){ //checking if the character we want to add is null
      throw new IllegalArgumentException("calls advance() with a null character");
    }

    while (tempVar != null) { //goes through siblings to find a match to c
        if (tempVar.data == c) { //if the character matches we update the current node to the matched node (tempVar)
            currentNode = tempVar;
            currentPrefix.append(c); //we then add the character c to the currentPrefix
            return true;
        }
        tempVar = tempVar.nextSibling; //moves to the next sibling
    }
    currentNode = null;
    return false; //returns false if method cant keep going
  }







  @Override
  public void retreat() {
    if (currentPrefix == null){
      throw new IllegalStateException("current prefix is already an empty string"); //we throw this exception because we cant remove the last character of a null string
    } else {
      currentPrefix.deleteCharAt(currentPrefix.length() - 1); //deletes the last character
      if(currentNode != null && currentNode.parent != null){ //if the currentNode isnt null and if its parent isnt null then we move back up the tree to the currentNode's parent
        currentNode = currentNode.parent;
      }
    }
  }







  @Override
  public void reset() {
    currentPrefix = null; //resets the current prefix to a null string
  }






  @Override
  public boolean isWord() {
  if(currentPrefix==null){ //returns false if there is no currentPrefix
    return false;
  }
  DLBNode scout = currentNode; //pointer for the search
  boolean found = false; //indicates if the word is found in the dictionary or not. it is initialized to not found for now.
    for(int i =0; i<currentPrefix.length(); i++){ //runs through currentPrefix
      char characterAt = currentPrefix.charAt(i); //gets the character at location i in currentPrefix
      while(scout!=null){ //loop until there are no more characters - reaches end of the prefix
        if(scout.data==characterAt){ //if the character is found we break out of the loop to get to the next character
          found=true; //we set found to true since we found the character
          break;
        }
        scout=scout.nextSibling; //moves onto the next sibling since that is how we traverse through a DLB Trie
      }
    }
    return scout!=null && scout.isWord;
  }









  @Override
  public void add() {
    if(currentPrefix.length() > 0 && !currentNode.isWord){
      add(currentPrefix.toString()); //this add method I'm calling here is the add method I wrote above which adds the currentPrefix to the dictionary if it isnt already in it
    }
  }







  @Override
  public int getNumberOfPredictions() {
    ArrayList<String> predictions = retrievePredictions();
    return predictions.size(); //returns the size of the arraylist from the last method below retrievePredictions because that is the list of possible predictions and now we are returning the number of possible predictions
  }







  @Override
  public String retrievePrediction() {
     DLBNode scout = currentNode; //scout becomes the pointer to traverse through the tree
     while(scout!=null){
       if(scout.isWord){ //if the current prefix is a word in the dictionary then return the current prefix as a possible prediction
         return currentPrefix.toString();
       }
       if(scout.child!=null){ //moves to the next child or sibling if there is no child, to continue traversing the trie
         scout=scout.child;
         currentPrefix.append(scout.data); //adds the child character to the prefix
       } else{
         scout=scout.nextSibling;
         if(scout!=null){
           currentPrefix.append(scout.data); //adds the siblings character to the prefix
         }
       }
     }
     return null; //returns null if there isnt a complete word found
  }







  @Override
  public ArrayList<String> retrievePredictions() {
    DLBNode scout = currentNode; //assigns the currentNode to scout so begins at root
    if(scout == null){
      return new ArrayList<>(); //returns an empty ArrayList if the current node is null
    }
    ArrayList<String> predictions = new ArrayList<>(); //Initalizes the ArrayList for the dictionary words we find that start with the prefix
    if(root != null){
      while(scout!=null){
        if(scout.isWord){ //if the current node is a word in the dictionary then add it to the ArrayList predictions
          predictions.add(currentPrefix.toString());
        }
        //moves to the child or next sibling if no child to continue searching
        if(scout.child!=null){
          scout=scout.child; //scout moves to the current node's child if it isnt null
          currentPrefix.append(scout.data); //child nodes data is added to the prefix
        } else{
          scout=scout.nextSibling; //goes to next sibling if child is null
          if(scout!=null){ //if the sibling node exists then add its character to the prefix
            currentPrefix.append(scout.data);
          }
        }
      }
    }
    return predictions; //returns the list of possible predictions
  }






  @Override
  public boolean delete(String word) {
    // TODO
    return false;
  }





   //The DLBNode class
   private class DLBNode{
    private char data; //letter inside the node
    private int size;  //number of words in the subtrie rooted at node
    private boolean isWord; //true if the node is at the end of a word
    private DLBNode nextSibling; //doubly-linked list of siblings
    private DLBNode previousSibling;
    private DLBNode child; // child reference
    private DLBNode parent; //parent reference

    private DLBNode(char data){ //constructor
        this.data = data;
        size = 0;
        isWord = false;
    }
  }

  /* ==============================
   * Helper methods for debugging
   * ==============================
   */

  //Prints the nodes in a DLB Trie for debugging. The letter inside each node is followed by an asterisk if
  //the node's isWord flag is set. The size of each node is printed between parentheses.
  //Siblings are printed with the same indentation, whereas child nodes are printed with a deeper
  //indentation than their parents.
  public void printTrie(){
    System.out.println("==================== START: DLB Trie ====================");
    printTrie(root, 0);
    System.out.println("==================== END: DLB Trie ====================");
  }

  //a helper method for printTrie
  private void printTrie(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
      System.out.println(" (" + node.size + ")");
      printTrie(node.child, depth+1);
      printTrie(node.nextSibling, depth);
    }
  }
}
