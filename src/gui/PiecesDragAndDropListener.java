package gui;

import game.BitBoard;
import game.Speak;
import interfaces.Declarations;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
//import java.util.List;
//import game.BB;


import javax.swing.JFrame;

public class PiecesDragAndDropListener implements MouseListener, MouseMotionListener, Declarations {

	private ChessBoardGui board;
	
	private int dragOffsetX;
	private int dragOffsetY;
	
	static Point mouseDownCompCoords;
	private JFrame frame;

	public PiecesDragAndDropListener(ChessBoardGui board, JFrame f) {
		this.board = board;
		frame = f;
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		mouseDownCompCoords = evt.getPoint();
		
		if(!board.getGame().getActivePlayer().isDragPiecesEnabled())
			return;
		
		int x = mouseDownCompCoords.x;
		int y = mouseDownCompCoords.y;
		
		int mouseOverPos = getMouseOverPos(x, y);
		Speak.say("mouse is over pos: " + mouseOverPos, true);
		board.getGame().getActivePlayer().setDebugging(false);
		if(mouseOverPos >= 0 && mouseOverPos < 64) {
			PieceGui guiPieceInFocus = board.getGuiPiece(mouseOverPos);
			if(guiPieceInFocus != null) {
//				Speak.say("dragPiece: " + guiPieceInFocus, true);
				if(guiPieceInFocus.getColour() == board.getGame().getPlayerTurn()) {
					// calculate offset, because we do not want the drag piece
					// to jump with it's upper left corner to the current mouse position
					dragOffsetX = x - guiPieceInFocus.getX();
					dragOffsetY = y - guiPieceInFocus.getY();
					guiPieceInFocus.setState(STATE_WALK);
					guiPieceInFocus.getAnim(STATE_WALK).setIdlePause(0);
					guiPieceInFocus.getAnim(STATE_WALK).setSpeed(200);
					guiPieceInFocus.getAnim(STATE_WALK).play();
					board.setDragPiece(guiPieceInFocus);
				}
			}
		} 
		// DEBUG: green rects on every pieces bits for active player colour
		else {
			board.getGame().getActivePlayer().setDebugging(true);
		}
	}
	
	private int getMouseOverPos(int x, int y) {
		int pos = -1;
		if(x > BOARD_START_X && x < (BOARD_START_X + BOARD_WIDTH) &&
				y > BOARD_START_Y && y < (BOARD_START_Y + BOARD_HEIGHT)) {
			pos = ChessBoardGui.getPosFromCoords(ChessBoardGui.convertXToColumn(x), 
												ChessBoardGui.convertYToRow(y));
		}
		return pos;
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		mouseDownCompCoords = null;
		
		if(board.getDragPiece() != null){
			int x = evt.getPoint().x - dragOffsetX;
			int y = evt.getPoint().y - dragOffsetY;
			
			// set game piece to the new location if possible
			PieceGui dragPiece = board.getDragPiece();
			board.setNewPieceLocation(dragPiece, getMouseOverPos(x, y));
			dragPiece.setState(STATE_IDLE);
			dragPiece.getAnim(STATE_IDLE).resetIdlePause();
			dragPiece.getAnim(STATE_IDLE).play();
			board.repaint();
			board.setDragPiece(null);
		}
	}

	@Override
	public void mouseDragged(MouseEvent evt) {
        Point currCoords = evt.getLocationOnScreen();
        
		if(board.getDragPiece() != null) {			
			int x = evt.getPoint().x - dragOffsetX;
			int y = evt.getPoint().y - dragOffsetY;
			
			PieceGui dragPiece = board.getDragPiece();
			dragPiece.setX(x);
			dragPiece.setY(y);
			
			board.repaint();
		} else {
			// moves the entire game window if not dragging a gui piece
	        frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

}
