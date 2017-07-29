Readme of PinBall game with improved AI:


the implementation of AI in this game has the following features:
 - a certain numbers of newly implemented obstacles for the player and badguys to move around
 - a kind of 2D grid based approach to determining if there are obstacles in the path of the badguys
 - lines from the badguy to its target to show the path it is going to take
 
 extra features:
 - using the physics built into the game engine the badguy predicts where its target is going to be
   and moves to intercept it
 - the badguy values its own life and tries to stay away from going over the edge and dying
 - also he abandons chasing the player when a bonus ball is on the game, and tries instead to destroy that
 
 
 how this is implemented:
 the program has a new Graph.java and ObstacleObject.java class to help in the implementation, the graph class
 computes the shortest path from the badguy to the target using djikstras shortest weighted path algorithm.
 the obstacleobject class represents an obstacle in the game, and calls the methods for the graph class.
 what it does is basically send its four edge points and the badguy and target positions the graph which 
 then computes the shortest path around the obstacle. 
 the badguy class has extensions to help it make desicions on which target to chase and what path to take.
 the pinball class has extentions as well to help the badguy AI. as well as inplementing the 2D grid. the 2D
 grid is an array matrix reprenting the game world. null values where no obstacles are, and obstacle onject where
 there are. when the badguy wants to know if in obstacle is in its path, the area between the badguy and target is
 scanned for any obstacles, if there are any it is then further tested if it is in the direct path and a way
 round it is found.