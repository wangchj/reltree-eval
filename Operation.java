/**
 * Author: Chih-Jye Wang
 * Date  : Feb 21, 2014
 */
 
 /**
 * Operation represents a set of tree operations that are listed in the publication.
 */
enum Operation
{
    Root, Parent, Children, Siblings, Leaves, Height, Depth, Path, Member, Tree
}

/**
 * A list of relational data models (algorithms) in which the operations are implemented.
 */ 
enum Algorithm
{
    AL, //Adjacency list
    NS, //Nested Setspara
    SR  //Adjacency list, stored routine
}