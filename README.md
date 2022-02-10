# Candy Crush Game - GUI
### Author: Mohammadreza Amani
### date: 2022/01/21
---

> ![GameBoard](https://github.com/MohammadrezaAmani/CandyCrush-GUI/blob/master/media/gameBoard.png)
> <br> game board 
## Introduction
Cabdy Crush, a popular smartphone game, includes a grid screen
It is said that in each house there is a candy of a certain color. The player can
Select two candies that are placed horizontally or vertically next to each other
Swap. If 3 or more candies are stacked horizontally or vertically,
The candies are removed, the player receives a specific score for each and a few new candies
It is added from above to the columns from which the candy has been removed to fill the plate with candy. also
There are special candies, each with a special ability, for example by removing 4 candies,
They are replaced by an explosive candy that, if removed, creates a 3 by 3 square around it
Also deletes.
## How to run the game?
- clone the project and run the `Main.java` file.
## Project definition
1. #### menu
The game menu is a page that is displayed during the initial run. The menu includes the following buttons:
- Random start
- Start from file
- View scores
2. #### Home
- The game is a graphical screen containing a 10-by-10 grid for candy, player points and one
Button to go to the menu.
3. #### Program behavior
- When you open the application, the menu is displayed. In the menu we will have the following behaviors:
  - Pressing the random start button opens a randomly set page
And the player can start playing.
  - By pressing the start button from the file, the program should receive a file from the user and enter the mode
Read the candies from it, then arrange the game screen with the read mode and then the user can
Start playing.
  - By clicking the view points button, the user should be able to see his previous 5 highest points.
- On the home screen the user selects two candies, if the selected candies to
The faces were vertical or horizontal, and a removal mode, described below, occurred
They must be replaced, otherwise nothing will happen.
- By placing 3 or more candies of the same color vertically or horizontally, the candies from
The page is deleted and one of the following modes occurs:
  - If 3 candies are removed, the candies above them will fill the gap and the gap created in
The page is filled with random candies.
  - If there are 4 deleted candies, one row explosive candy will replace one of them.
) If the removed candies are columnar, the explosive candy is in the lowest place
Is empty, and if they are in a row, in the left (and then, as in the case of 3 candies, places
Empty fills.
  - If 5 or more candies are put together, it will be treated like 4 candies, but instead
A radial explosive candy is placed.
4. #### Types of candy 
- Each type of candy has one color and its color is one of the colors red, blue, yellow and green. Also, each candy has a behavior.
  - Simple candy : This type of candy if placed next to other candies and one of deleted modes occur, giving the player 5 points upon deletion.
  - Row explosive candy : This candy has two types of rows and columns. Its line type of delete Make 4 candies placed next to each other in a row and its column type from removing 4 Candy is put together in a column. If this candy is removed Depending on the type, it also removes all row or column candies. This type of candy with Deleting gives the user 10 points.
  - Radial blasting candy : This candy is removed by removing 5 candies in a row or column. Is achieved. Special feature of this candy after removing, removing a 5 by 5 square to Is its center. Also, this candy will give 15 points to the user after deleting.
5. #### The purpose of the game
- The goal of the game is to reach 1500 points. If the player achieves this goal, the program will
It shows a winning message and then directs him to the menu page.
6. #### Read the file
- A file in which a game mode is stored and the program must be able to read it, as The following is: <br> In the first line of the file there is an integer which is the player's score. <br> Here are 10 lines, each written in csv format (each row in a line and columns) <br> They are separated by the "," sign (and each represents a line from the game screen. <br> The information of each object in the file is in 3 letters. The first two letters indicate the type of candy and <br> The third letter indicates its color.
- Description of the first two letters:
  - SC: Simple candy
  - LR: A row row row candy
  - LC: Row of columnar type explosive candy
  - RC: Radial blast candy
-Third letter description:
  - R: red color
  - G: green color
  - B: Blue color
  - Y: yellow
7. #### Save points
- To save points, you must score the player's points with the 5 highest points after the end of the game Compare and save. For storage, it is recommended to use the file.
8. #### Help feature
- A button is added to the game home screen and by pressing it, two candies that feature<br> Demonstrate displacement to the player) One of the possible random modes <br> Also displayed if there is no movement for the player <br> Show a loss and lead him to me.
