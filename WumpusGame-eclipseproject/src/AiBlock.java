import javax.swing.text.DefaultEditorKit.BeepAction;

/**
 * this class is created Ai to hold block information but additionally there is one extra field 
 * for eheather ai been that block or not 
 * @author Can Eldem
 * @version 1.0
 * @since 
 */

public class AiBlock extends Block {

	boolean been_here; //show Ai that wheather have Ai been here or not 


	public AiBlock() {
		super();
		been_here=false;
	}

	AiBlock(Block b,boolean been){

		super(b);
		been_here=been;
	}
}
