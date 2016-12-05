package poker.app.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import poker.app.MainApp;
import pokerBase.Action;
import pokerBase.Card;
import pokerBase.CardDraw;
import pokerBase.GamePlay;
import pokerBase.Hand;
import pokerBase.HandScore;
import pokerBase.Player;
import pokerBase.Rule;
import pokerBase.Table;
import pokerEnums.eAction;
import pokerEnums.eCardDestination;
import pokerEnums.eDrawCount;
import pokerEnums.eGame;
import pokerEnums.eGameState;
import pokerEnums.eHandStrength;
import pokerEnums.ePlayerPosition;
import pokerEnums.eRank;

public class PokerTableController implements Initializable {

	// Reference to the main application.
	private MainApp mainApp;

	public PokerTableController() {
	}

	@FXML
	private AnchorPane mainAnchorPane;

	@FXML
	private Label lblWinningPlayer;
	@FXML
	private Label lblWinningHand;

	@FXML
	private Label lblPlayerPos1;
	@FXML
	private Label lblPlayerPos2;
	@FXML
	private Label lblPlayerPos3;
	@FXML
	private Label lblPlayerPos4;

	@FXML
	private ImageView imgViewDealerButtonPos1;
	@FXML
	private ImageView imgViewDealerButtonPos2;
	@FXML
	private ImageView imgViewDealerButtonPos3;
	@FXML
	private ImageView imgViewDealerButtonPos4;

	@FXML
	private BorderPane OuterBorderPane;

	@FXML
	private TextArea txtPlayerArea;

	@FXML
	private Button btnStartGame;
	@FXML
	private Button btnDeal;

	@FXML
	private ToggleButton btnPos1SitLeave;
	@FXML
	private ToggleButton btnPos2SitLeave;
	@FXML
	private ToggleButton btnPos3SitLeave;
	@FXML
	private ToggleButton btnPos4SitLeave;

	@FXML
	private Label lblPos1Name;
	@FXML
	private Label lblPos2Name;
	@FXML
	private Label lblPos3Name;
	@FXML
	private Label lblPos4Name;

	@FXML
	private HBox hBoxDeck;

	@FXML
	private HBox hboxP1Cards;
	@FXML
	private HBox hboxP2Cards;
	@FXML
	private HBox hboxP3Cards;
	@FXML
	private HBox hboxP4Cards;
	@FXML
	private HBox hboxCommunity;

	private int[] iCurrentCard = { 0, 0, 0, 0 };

	Point2D pntCardDeck = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		imgViewDealerButtonPos3.setVisible(true);
		imgViewDealerButtonPos4.setVisible(true);

		lblPlayerPos1.setText("1");
		lblPlayerPos2.setText("2");
		lblPlayerPos3.setText("3");
		lblPlayerPos4.setText("4");

		lblWinningPlayer.setText("");
		lblWinningHand.setText("");

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void handlePlay() {
	}

	@FXML
	public void GetGameState() {
		Action act = new Action(eAction.GameState, mainApp.getPlayer());
		mainApp.messageSend(act);
	}

	public void btnSitLeave_Click(ActionEvent event) {
		ToggleButton btnSitLeave = (ToggleButton) event.getSource();
		int iPlayerPosition = 0;
		if (btnSitLeave.isSelected()) {
			switch (btnSitLeave.getId().toString()) {
			case "btnPos1SitLeave":
				iPlayerPosition = ePlayerPosition.ONE.getiPlayerPosition();
				break;
			case "btnPos2SitLeave":
				iPlayerPosition = ePlayerPosition.TWO.getiPlayerPosition();
				break;
			case "btnPos3SitLeave":
				iPlayerPosition = ePlayerPosition.THREE.getiPlayerPosition();
				break;
			case "btnPos4SitLeave":
				iPlayerPosition = ePlayerPosition.FOUR.getiPlayerPosition();
				break;
			}
		} else {
			iPlayerPosition = 0;
		}

		// Set the PlayerPosition in the Player
		mainApp.getPlayer().setiPlayerPosition(iPlayerPosition);

		// Build an Action message
		Action act = new Action(btnSitLeave.isSelected() ? eAction.Sit : eAction.Leave, mainApp.getPlayer());

		// Send the Action to the Hub
		mainApp.messageSend(act);
	}

	public void MessageFromMainApp(String strMessage) {
		System.out.println("Message received by PokerTableController: " + strMessage);
	}

	private Label getPlayerLabel(int iPosition) {
		switch (iPosition) {
		case 1:
			return lblPlayerPos1;
		case 2:
			return lblPlayerPos2;
		case 3:
			return lblPlayerPos3;
		case 4:
			return lblPlayerPos4;
		default:
			return null;
		}
	}

	private ToggleButton getSitLeave(int iPosition) {
		switch (iPosition) {
		case 1:
			return btnPos1SitLeave;
		case 2:
			return btnPos2SitLeave;
		case 3:
			return btnPos3SitLeave;
		case 4:
			return btnPos4SitLeave;
		default:
			return null;
		}
	}

	/**
	 * Return the HBox of player cards based on iPosition
	 * 
	 * @param iPosition
	 * @return
	 */
	private HBox getCardHBox(int iPosition) {
		switch (iPosition) {
		case 0:
			return hboxCommunity;
		case 1:
			return hboxP1Cards;
		case 2:
			return hboxP2Cards;
		case 3:
			return hboxP3Cards;
		case 4:
			return hboxP4Cards;
		default:
			return null;
		}

	}

	/**
	 * Handle_TableState sets the table state of the game, who's sitting in what
	 * position the Player's name in the label
	 * 
	 * @param HubPokerTable
	 */
	public void Handle_TableState(Table HubPokerTable) {

		lblPlayerPos1.setText("");
		lblPlayerPos2.setText("");
		lblPlayerPos3.setText("");
		lblPlayerPos4.setText("");
		boolean bSeated = false;

		for (int iPlayerPos = 1; iPlayerPos < 5; iPlayerPos++) {
			Player p = HubPokerTable.getPlayerByPosition(iPlayerPos);
			if (p != null) {
				getPlayerLabel(iPlayerPos).setText(p.getPlayerName());

				if (p.getPlayerID().equals(mainApp.getPlayer().getPlayerID())) {
					getSitLeave(iPlayerPos).setText("Leave");
					bSeated = true;
				} else {
					getSitLeave(iPlayerPos).setVisible(false);
				}
			}
		}

		// Note: Players have been seated, set the Sit/Leave text
		// based on players already seated.

		for (int iPlayerPos = 1; iPlayerPos < 5; iPlayerPos++) {

			if (getPlayerLabel(iPlayerPos).getText() == "") {
				if (bSeated) {
					getSitLeave(iPlayerPos).setVisible(false);
				} else {
					getSitLeave(iPlayerPos).setVisible(true);
					getSitLeave(iPlayerPos).setText("Sit");
				}
			}
		}
	}

	public void Handle_GameState(GamePlay HubPokerGame) {

		GamePlay.StateOfGamePlay(HubPokerGame);

		eDrawCount eDrawCnt = HubPokerGame.geteDrawCountLast();

		if (eDrawCnt == eDrawCount.FIRST) {
			hboxP1Cards.getChildren().clear();
			hboxP2Cards.getChildren().clear();
			hboxP3Cards.getChildren().clear();
			hboxP4Cards.getChildren().clear();
			hboxCommunity.getChildren().clear();
			for (int i = 0; i < iCurrentCard.length; i++) {
				iCurrentCard[i] = 0;
			}

			// Deal out five placeholders
			for (int iPos = 0; iPos < 5; iPos++) {
				for (int iCard = 0; iCard < 5; iCard++) {
					this.getCardHBox(iPos).getChildren().add(BuildImageView(-1));
				}
			}

			hBoxDeck.getChildren().clear();
			hBoxDeck.getChildren().add(BuildImageView(-2));
			lblWinningPlayer.setText("");
			lblWinningHand.setText("");

			ImageView imgvDealerDeck = (ImageView) hBoxDeck.getChildren().get(0);
			Bounds bndCardDeck = imgvDealerDeck.localToScene(imgvDealerDeck.getBoundsInLocal());
			pntCardDeck = new Point2D(bndCardDeck.getMinX(), bndCardDeck.getMinY());
			System.out.println("Card is at (x,y): " + pntCardDeck.getX() + " " + pntCardDeck.getY());

			Circle c = new Circle(pntCardDeck.getX(), pntCardDeck.getY() - 35, 5);
			c.setFill(Color.YELLOW);
			mainAnchorPane.getChildren().add(c);

		}

		System.out.println("State of game: " + HubPokerGame.geteGameState());
		CardDraw cd = HubPokerGame.getRule().GetDrawCard(eDrawCnt);

		ImageView ivDealtCard = null;

		Hand hcheck = HubPokerGame.getPlayerHand(mainApp.getPlayer());
		for (Card c : hcheck.getCardsInHand()) {
			System.out.println(c.geteRank() + " " + c.geteSuit());
		}

		if (HubPokerGame.geteGameState() != eGameState.SCORED) {
			if (cd.getCardDestination() == eCardDestination.Player) {
				Iterator it = HubPokerGame.getGamePlayers().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					Player p = HubPokerGame.getGamePlayer(UUID.fromString(pair.getKey().toString()));
					Hand h = HubPokerGame.getPlayerHand(p);
					ArrayList<Card> cardsDrawn = h.GetCardsDrawn(eDrawCnt, HubPokerGame.getRule().GetGame(),
							eCardDestination.Player);

					for (Card c : cardsDrawn) {
						String strImagePath;
						int iCardNbr;
						if (p.getPlayerID().equals(mainApp.getPlayer().getPlayerID())) {
							strImagePath = BuildImagePath(c.getiCardNbr());
							iCardNbr =c.getiCardNbr();
						} else {
							strImagePath = BuildImagePath(0);
							iCardNbr = 0;
						}

						Platform.runLater(() -> {

							ImageView ivCurrentCard = (ImageView) this.getCardHBox(p.getiPlayerPosition()).getChildren()
									.get(iCurrentCard[p.getiPlayerPosition()]);
							ivCurrentCard.setImage(BuildImage(c.getiCardNbr()));

							Bounds bndCurrentCard = ivCurrentCard.localToScene(ivCurrentCard.getBoundsInLocal());
							Point2D pntCurrentCard = new Point2D(bndCurrentCard.getMinX(),
									bndCurrentCard.getMinY() - 35);
							SequentialTransition ST = CalculateTransition(ivCurrentCard,
									strImagePath, pntCurrentCard, pntCardDeck, iCardNbr);
							ST.play();
							iCurrentCard[p.getiPlayerPosition()]++;
						});

						// }
						/*
						 * else {
						 * this.getCardHBox(p.getiPlayerPosition()).getChildren(
						 * ).add(BuildImageView(0));
						 * 
						 * ivDealtCard = (ImageView)
						 * this.getCardHBox(p.getiPlayerPosition()).getChildren(
						 * ) .get(this.getCardHBox(p.getiPlayerPosition()).
						 * getChildren().size() - 1);
						 * 
						 * 
						 * }
						 */
					}
				}
			} else if (cd.getCardDestination() == eCardDestination.Community) {
				Player p = HubPokerGame.getPlayerCommon();
				Hand h = HubPokerGame.getGameCommonHand();
				ArrayList<Card> cardsDrawn = h.GetCardsDrawn(eDrawCnt, HubPokerGame.getRule().GetGame(),
						eCardDestination.Community);
				for (Card c : cardsDrawn) {
					this.getCardHBox(0).getChildren().add(BuildImageView(c.getiCardNbr()));
					ivDealtCard = (ImageView) this.getCardHBox(0).getChildren()
							.get(this.getCardHBox(0).getChildren().size() - 1);
				}
			}

		}

		if (HubPokerGame.geteGameState() == eGameState.FINISHED) {
			Action act = new Action(eAction.ScoreGame, mainApp.getPlayer());
			// Send the Action to the Hub
			mainApp.messageSend(act);
		} else if (HubPokerGame.geteGameState() == eGameState.SCORED) {
			lblWinningPlayer.setText(HubPokerGame.GetWinningHand().getHandPlayer().getPlayerName());
			HandScore hs = HubPokerGame.GetWinningHand().getHandScore();
			lblWinningHand.setText(eHandStrength.geteHandStrength(hs.getHandStrength()).toString() + " "
					+ eRank.geteRank(hs.getHiHand()).toString());
		}
	}

	/**
	 * BuildImageView - Return an ImageView with a card image based on iCardNbr
	 * 
	 * @param iCardNbr
	 * @return
	 */
	private ImageView BuildImageView(int iCardNbr) {

		ImageView i1 = new ImageView(BuildImage(iCardNbr));
		return i1;
	}

	/**
	 * BuildImage = Build Image based on iCardNbr
	 * 
	 * @param iCardNbr
	 * @return
	 */
	private Image BuildImage(int iCardNbr) {

		return new Image(getClass().getResourceAsStream(BuildImagePath(iCardNbr)), 72, 96, false, true);
	}

	private String BuildImagePath(int iCardNbr) {
		String strImgPath;
		if (iCardNbr == -2) {
			strImgPath = "/img/b2fh.png";
		} else if (iCardNbr == -1) {
			strImgPath = "/img/card_placeholder.png";
		} else if (iCardNbr == 0) {
			strImgPath = "/img/b2fv.png";
		} else {
			strImgPath = "/img/" + iCardNbr + ".png";
		}

		return strImgPath;
	}

	/**
	 * btnStart_Click - Do this action when the 'Start' button is pressed.
	 * 
	 * @param event
	 */
	@FXML
	void btnStart_Click(ActionEvent event) {
		// Start the Game
		Action act = new Action(eAction.StartGame, mainApp.getPlayer());

		// figure out which game is selected in the menu
		eGame gme = eGame.getGame(Integer.parseInt(mainApp.getRuleName().replace("PokerGame", "")));

		// Get the rule to figure out placeholder cards.
		// Rule rle = new Rule(gme);

		for (int iPlayerNbr = 0; iPlayerNbr < 5; iPlayerNbr++) {
			for (int iCard = 0; iCard < 5; iCard++) {
				getCardHBox(iPlayerNbr).getChildren().add(BuildImageView(-1));
			}
		}

		// Set the gme in the action
		act.seteGame(gme);

		// Send the Action to the Hub
		mainApp.messageSend(act);
	}

	/**
	 * btnDeal_Click - Do this action when the 'Deal' button is pressed.
	 * 
	 * @param event
	 */
	@FXML
	void btnDeal_Click(ActionEvent event) {

		// Set the new Deal action
		Action act = new Action(eAction.Draw, mainApp.getPlayer());

		// Send the Action to the Hub
		mainApp.messageSend(act);

	}

	@FXML
	public void btnFold_Click(ActionEvent event) {
		Button btnFold = (Button) event.getSource();
		switch (btnFold.getId().toString()) {
		case "btnPlayer1Fold":
			// Fold for Player 1
			break;
		case "btnPlayer2Fold":
			// Fold for Player 2
			break;
		case "btnPlayer3Fold":
			// Fold for Player 3
			break;
		case "btnPlayer4Fold":
			// Fold for Player 4
			break;

		}
	}

	@FXML
	public void btnCheck_Click(ActionEvent event) {
		Button btnCheck = (Button) event.getSource();
		switch (btnCheck.getId().toString()) {
		case "btnPlayer1Check":
			// Check for Player 1
			break;
		case "btnPlayer2Check":
			// Check for Player 2
			break;
		case "btnPlayer3Check":
			// Check for Player 3
			break;
		case "btnPlayer4Check":
			// Check for Player 4
			break;
		}
	}

	private void FadeButton(Button btn) {
		FadeTransition ft = new FadeTransition(Duration.millis(3000), btn);
		ft.setFromValue(1.0);
		ft.setToValue(0.3);
		ft.setCycleCount(4);
		ft.setAutoReverse(true);

		ft.play();
	}

	private SequentialTransition CalculateTransition(ImageView ivPlayerCardImageView, String strImage,
			Point2D pntCardDealt, Point2D pntCardDeck, int iCardDrawn) {

		// Add a sequential transition to the card (move, rotate)
		SequentialTransition transMoveRotCard = createSequentialTransition(pntCardDeck, pntCardDealt);

		// Add a parallel transition to the card (fade in/fade out).
		final ParallelTransition transFadeCardInOut = createFadeTransition(ivPlayerCardImageView,
				new Image(getClass().getResourceAsStream(strImage), 72, 96, false, true));

		SequentialTransition transAllActions = new SequentialTransition();
		transAllActions.getChildren().addAll(transMoveRotCard, transFadeCardInOut);

		return transAllActions;
	}

	private SequentialTransition createSequentialTransition(final Point2D pntStartPoint, final Point2D pntEndPoint) {

		ImageView imView = new ImageView(
				new Image(getClass().getResourceAsStream("/img/b2fv.png"), 72, 96, false, true));

		imView.setX(pntStartPoint.getX());
		imView.setY(pntStartPoint.getY());

		mainAnchorPane.getChildren().add(imView);

		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), imView);
		translateTransition.setFromX(0);
		translateTransition.setToX(pntEndPoint.getX() - pntStartPoint.getX());
		translateTransition.setFromY(0);
		translateTransition.setToY(pntEndPoint.getY() - pntStartPoint.getY());

		translateTransition.setCycleCount(1);
		translateTransition.setAutoReverse(false);

		int rnd = randInt(1, 3);

		RotateTransition rotateTransition = new RotateTransition(Duration.millis(150), imView);
		rotateTransition.setByAngle(360F);
		rotateTransition.setCycleCount(rnd);
		rotateTransition.setAutoReverse(false);

		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition, rotateTransition);

		SequentialTransition seqTrans = new SequentialTransition();
		seqTrans.getChildren().addAll(parallelTransition);

		final ImageView ivRemove = imView;
		seqTrans.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				mainAnchorPane.getChildren().remove(ivRemove);
			}
		});

		return seqTrans;
	}

	private ParallelTransition createFadeTransition(final ImageView iv, final Image img) {

		FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(.1), iv);
		fadeOutTransition.setFromValue(1.0);
		fadeOutTransition.setToValue(0.0);
		fadeOutTransition.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				iv.setImage(img);
			}

		});

		FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(.1), iv);
		fadeInTransition.setFromValue(0.0);
		fadeInTransition.setToValue(1.0);

		/*
		 * FadeTransition fadeFlyCare = FadeOutTransition(ivFlyCard);
		 */

		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(fadeOutTransition, fadeInTransition);

		return parallelTransition;
	}

	/**
	 * randInt - Create a random number
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private static int randInt(int min, int max) {

		return (int) (Math.random() * (min - max)) * -1;

	}
}