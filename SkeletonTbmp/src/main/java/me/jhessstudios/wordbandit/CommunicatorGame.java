package me.jhessstudios.wordbandit;

import android.graphics.Bitmap;

/**
 * Created by Juhess88 on 2/23/2015.
 */
public interface CommunicatorGame {

    //the word is added to the current player's ListView
    public void wordAddedToListViewOfCurrentPlayer(String data);

    //whenever button clicked, its letter added to EditTextFragment
    public void addLetterOfClickedButtonToEditTextFragment(String data);

    //whenever button clicked, its letter removed from EditTextFragment
    public void removeLetterOfClickedButtonFromEditTextFragment(String data);

    //the word the user created by clicking on buttons which is displayed in the EditTextFragment
    //but if that word is not in the dictionary, the String returns ""
    public String wordUserCreatedFromEditTextFragment();

    //removes the text from the EditTextFragment
    public void clearTextFromEditTextFragment();

    //the word user is trying to steal
    public String wordUserIsTryingToSteal();

    //displays gold tiles which contain the letters of the word user trying to steal
    public void makeGoldTilesForWordUserTryingToSteal(String data);

    //show original game screen
    //(hide the DynamicButtonsFragment and show the fragments that were previously hidden)
    public void showOriginalGameScreen();

    //true if the user is trying to steal a word
    //false if simply making a new word
    public Boolean isTryingToStealWord();

    //checks if all the buttons were clicked in the new word user is trying to steal
    public Boolean checkIfAllButtonsClickedInWordUserTryingToSteal();

    //removes word from Listview1
    public void wordIsStolenFromListView1();

    //removes word from Listview2
    public void wordIsStolenFromListView2();

    //true if first listview clicked, false if second listview clicked
    public Boolean isListView1Clicked();

    public void setListView1ToFalse();

//    public Boolean returnListView2Flag();
//    public void listview1NotClicked();
//    public void listview2NotClicked();
//    public void fChangeScore();
//    public int getGoogleCounter();

    //length of the word in the EditTextFragment
    public int lengthOfWordInEditTextFragment();

    //returns length of new word (or length of visible tiles of the new word)
    public int lengthOfWordUserTryingToSteal();

    //returns buttons to unclicked state
    public void returnButtonsToUnclickedState();

    //fills the empty tiles with new letters
    public void fillEmptyTilesWithNewLetters();

    //updates the score of the match for both players
    public void updateScoreOfBothPlayers();

    //removes the letters that were clicked when the user swipes across the screen
    public void removeButtonTextIfClickedAndIsWord();

    //fling gesture detected
    public void fling(boolean isSwipe);

    //turn completed
    public void turnComplete();

    //keeps track internally of how many tiles are left in the game
    public int getTilesRemainingInt();

    //end current game
    public void finishGame();

    //the final turn of the first player
    public boolean lastTurnFirstPlayer();

    //the final turn of the second player
    public boolean lastTurnSecondPlayer();

    //turns Bitmap to Circle instead of Square
    public Bitmap getCircleBitmap(Bitmap bitmap);

    //to exit the game and go back to main menu
    public void exitGameQuestion(String title);

    public String getMessageCombo();

    //remove cardview for under sdk 21
    public void removeCV();

    //add cardview for under sdk21
    public void addCV();

    public int getTilesRemaining();

    public void messageAtShuffle(String message);

    public void messageAtPass(String message);
}
