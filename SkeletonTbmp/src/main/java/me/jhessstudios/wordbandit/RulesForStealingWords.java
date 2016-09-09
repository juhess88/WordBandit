package me.jhessstudios.wordbandit;

/**
 * Created by Juhess88 on 4/6/2015.
 */
public class RulesForStealingWords {


    public RulesForStealingWords() {
    }

    public Boolean wordUserTryingToStealEndWith_E(String wordUserIsTryingToSteal){
        String lastLetterOfWordTryingToSteal = wordUserIsTryingToSteal.substring(wordUserIsTryingToSteal.length() - 1);
        if(lastLetterOfWordTryingToSteal.equals("e"))
            return true;
        return false;
    }

    public Boolean wordUserIsTryingToSteal_R(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        String wordUserIsTryingToSteal_R = wordUserIsTryingToSteal+"r";
        String wordUserIsTryingToSteal_R_S = wordUserIsTryingToSteal_R+"s";
        if (wordUserIsTryingToSteal_R.equals(wordUserCreatedFromEditTextFragment)
                || wordUserIsTryingToSteal_R_S.equals(wordUserCreatedFromEditTextFragment))
        {
            return true;
        }
        return false;
    }

    public Boolean wordUserIsTryingToSteal_D(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        String wordUserIsTryingToSteal_D = wordUserIsTryingToSteal+"d";
        if (wordUserIsTryingToSteal_D.equals(wordUserCreatedFromEditTextFragment))
        {
            return true;
        }
        return false;
    }

    public Boolean wordUserIsTryingToSteal_S(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        String wordUserIsTryingToSteal_S = wordUserIsTryingToSteal+"s";

        if(wordUserIsTryingToSteal_S.equals(wordUserCreatedFromEditTextFragment))
        {
            return true;
        }
        return false;
    }

    public Boolean wordUserIsTryingToSteal_Y(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        //this String contains the last letter of the word user is trying to steal -
        //many times in spelling you had the last letter again before using suffixes
        String addLastLetterOfWordTryingToSteal = wordUserIsTryingToSteal.substring(wordUserIsTryingToSteal.length() - 1);

        String wordUserIsTryingToSteal_Y = wordUserIsTryingToSteal+"y";
        String wordUserIsTryingToSteal_Y_PlusLastLetter = wordUserIsTryingToSteal+addLastLetterOfWordTryingToSteal+"y";

        if(wordUserIsTryingToSteal_Y.equals(wordUserCreatedFromEditTextFragment)
                || wordUserIsTryingToSteal_Y_PlusLastLetter.equals(wordUserCreatedFromEditTextFragment))
        {
            return true;
        }
        return false;
    }

    public Boolean wordUserIsTryingToSteal_RE(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        String wordUserIsTryingToSteal_RE = "re"+wordUserIsTryingToSteal;

        if(wordUserIsTryingToSteal_RE.equals(wordUserCreatedFromEditTextFragment))
        {
            return true;
        }
        return false;
    }

    public Boolean wordUserIsTryingToSteal_A(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        String wordUserIsTryingToSteal_A = "a"+wordUserIsTryingToSteal;

//        if(wordUserIsTryingToSteal_A.equals(wordUserCreatedFromEditTextFragment))
//        {
//            return true;
//        }
        return false;
    }

    public Boolean wordUserIsTryingToSteal_ING(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        //this String contains the last letter of the word user is trying to steal -
        //many times in spelling you had the last letter again before using suffixes
        String addLastLetterOfWordTryingToSteal = wordUserIsTryingToSteal.substring(wordUserIsTryingToSteal.length() - 1);

        String wordUserIsTryingToSteal_ING = wordUserIsTryingToSteal+"ing";
        String wordUserIsTryingToSteal_ING_S = wordUserIsTryingToSteal_ING+"s";
        String wordUserIsTryingToSteal_ING_PlusLastLetter = wordUserIsTryingToSteal+addLastLetterOfWordTryingToSteal+"ing";
        String wordUserIsTryingToSteal_ING_S_PlusLastLetter = wordUserIsTryingToSteal_ING_PlusLastLetter+"s";

        if(wordUserIsTryingToSteal_ING.equals(wordUserCreatedFromEditTextFragment) ||
                wordUserIsTryingToSteal_ING_S.equals(wordUserCreatedFromEditTextFragment) ||
                wordUserIsTryingToSteal_ING_PlusLastLetter.equals(wordUserCreatedFromEditTextFragment) ||
                wordUserIsTryingToSteal_ING_S_PlusLastLetter.equals(wordUserCreatedFromEditTextFragment))
        {
            return true;
        }

        return false;
    }

    public Boolean wordUserIsTryingToSteal_ER(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        //this String contains the last letter of the word user is trying to steal -
        //many times in spelling you had the last letter again before using suffixes
        String addLastLetterOfWordTryingToSteal = wordUserIsTryingToSteal.substring(wordUserIsTryingToSteal.length() - 1);

        String wordUserIsTryingToSteal_ER = wordUserIsTryingToSteal+"er";
        String wordUserIsTryingToSteal_ER_S = wordUserIsTryingToSteal_ER+"s";
        String wordUserIsTryingToSteal_ER_PlusLastLetter = wordUserIsTryingToSteal+addLastLetterOfWordTryingToSteal+"er";
        String wordUserIsTryingToSteal_ER_S_PlusLastLetter = wordUserIsTryingToSteal_ER_PlusLastLetter+"s";

        if((wordUserIsTryingToSteal_ER.equals(wordUserCreatedFromEditTextFragment) && wordUserIsTryingToSteal.length()>3) ||
                (wordUserIsTryingToSteal_ER_S.equals(wordUserCreatedFromEditTextFragment) && wordUserIsTryingToSteal.length()>3)||
                wordUserIsTryingToSteal_ER_PlusLastLetter.equals(wordUserCreatedFromEditTextFragment) ||
                wordUserIsTryingToSteal_ER_S_PlusLastLetter.equals(wordUserCreatedFromEditTextFragment))
        {
            return true;
        }

        return false;
    }

    public Boolean wordUserIsTryingToSteal_ED(String wordUserIsTryingToSteal, String wordUserCreatedFromEditTextFragment){
        //this String contains the last letter of the word user is trying to steal -
        //many times in spelling you had the last letter again before using suffixes
        String addLastLetterOfWordTryingToSteal = wordUserIsTryingToSteal.substring(wordUserIsTryingToSteal.length() - 1);

        String wordUserIsTryingToSteal_ED = wordUserIsTryingToSteal+"ed";
        String wordUserIsTryingToSteal_ED_PlusLastLetter = wordUserIsTryingToSteal+addLastLetterOfWordTryingToSteal+"ed";
        if((wordUserIsTryingToSteal_ED.equals(wordUserCreatedFromEditTextFragment) && wordUserIsTryingToSteal.length()>3) ||
                wordUserIsTryingToSteal_ED_PlusLastLetter.equals(wordUserCreatedFromEditTextFragment))
        {
            return true;
        }
        return false;
    }

}
