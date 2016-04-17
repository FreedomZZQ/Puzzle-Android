package studio.androiddev.puzzle.event;

/**
 * puzzle
 * Created by ZQ on 2016/4/17.
 */
public class PieceMoveSuccessEvent {

    private int index;
    public PieceMoveSuccessEvent(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
