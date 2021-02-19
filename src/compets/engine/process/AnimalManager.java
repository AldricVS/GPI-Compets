package compets.engine.process;

import java.util.Random;

import compets.engine.data.animal.Animal;
import compets.engine.data.animal.Behavior;
import compets.engine.data.animal.Gauge;
import compets.engine.data.animal.States;
import compets.engine.data.map.Box;
import compets.engine.data.map.EmptyBox;
import compets.engine.data.map.Map;
import compets.engine.data.map.Position;
import compets.engine.data.map.Wall;
import compets.engine.data.map.item.BadItem;
import compets.engine.data.map.item.GoodItem;
import compets.engine.data.map.item.NeutralItem;

/**
 * Cette classe permet de gérer le déroulement du jeu
 * 
 * @author Nathan VIRAYIE
 *
 */
public class AnimalManager {
	private Animal animal;
	private Map map;
	private boolean hasInteracted = true;

	Random rand = new Random();

	public AnimalManager(Animal animal, Map map) {
		this.animal = animal;
		this.map = map;
	}

	/**
	 * The main method to call from the gui. The animal will do something depending
	 * on what he do before : if he moved last turn, he will try to interact with
	 * the item under him.
	 * 
	 */
	public void doSomething() {
		resetAnimalState();
		if (hasInteracted) {
			// Try to move directly
			moveAnimalToNewPosition();
			hasInteracted = false;
		} else {
			// If can interact with something, do it
			Box boxAtPosition = map.getBoxAtPosition(animal.getPosition());
			if (!(boxAtPosition instanceof EmptyBox)) {
				interact();
				hasInteracted = true;
			} else {
				// Else, move
				moveAnimalToNewPosition();
			}
		}
	}

	/**
	 * Force the animal to move to a new random position, so that he is not stuck at
	 * the same place during several turns
	 */
	public void moveAnimalToNewPosition() {
		Position oldPosition = animal.getPosition();
		Position newPosition;
		do {
			moveAnimal(chooseNextMove());
			newPosition = animal.getPosition();
		} while (newPosition.getX() == oldPosition.getX() && newPosition.getY() == oldPosition.getY());
	}

	/**
	 * Choose randomly the next position the animal want to take
	 */
	public Position chooseNextMove() {
		Position currentPos = animal.getPosition();

		int currentXPos = currentPos.getX();
		int currentYPos = currentPos.getY();

		int newXPos;
		int newYPos;

		// We want the animal to move every turn
		do {
			newXPos = rand.nextInt(3) - 1;
			newYPos = rand.nextInt(3) - 1;
		} while (newXPos == 0 && newYPos == 0);

		Position nextPos = new Position(currentXPos + newXPos, currentYPos + newYPos);
		return nextPos;
	}

	/**
	 * Move the animal to the position, except if he can't (there is a wall or the
	 * position is out of the map)
	 * 
	 * @param position the position where to move
	 */
	public void moveAnimal(Position position) {
		int columnCount = map.getColumnCount();
		int rowCount = map.getRowCount();

		// check if the position is in the map
		int positionX = position.getX();
		int positionY = position.getY();
		if (positionX > 0 && positionX < columnCount - 1 && positionY > 0 && positionY < rowCount - 1) {
			// check if the box t the position is not a wall
			Box boxAtPosition = map.getBoxAtPosition(position);
			if (!(boxAtPosition instanceof Wall)) {
				// we can safely move the animal
				animal.setPosition(position);
			}
		}
	}

	/**
	 * Définit les intéractions possible par l'animal en fonction de la case sur
	 * laquelle il se trouve
	 * 
	 * Ces derniers seront réalisées en fonction du dressage reçu.
	 */
	public void interact() {
		Position currentPos = this.animal.getPosition();
		Behavior bh = this.animal.getBehavior();
		Gauge jauge = bh.getActionGauge();

		int obedience = jauge.getValue();
		int max = Gauge.MAX_GAUGE;
		int actionChoice = rand.nextInt(max + 1);

		// Mauvaise action par l'animal
		if (map.getBoxAtPosition(currentPos) instanceof BadItem) {
			if (actionChoice >= obedience) {
				changeState(States.BAD_ACTION);
			}
		}

		// Bonne action par l'animal
		else if (map.getBoxAtPosition(currentPos) instanceof GoodItem) {
			if (actionChoice <= obedience) {
				changeState(States.GOOD_ACTION);
			}
		}

		// Possibilité du choix de l'intéraction par l'animal (bonne ou mauvaise)
		else if (map.getBoxAtPosition(currentPos) instanceof NeutralItem) {
			if (actionChoice < obedience - 20) {
				changeState(States.GOOD_ACTION);
			} else if (actionChoice >= obedience - 20) {
				changeState(States.BAD_ACTION);
			}
		}
	}

	/**
	 * Permet de changer l'etat de l'animal
	 * 
	 * @param s le nouvelle état dans lequel il prendra
	 */
	public void changeState(States state) {
		animal.setState(state);
//		System.out.println(animal.getStates());
	}

	/**
	 * Permet de réinitialiser l'etat de l'animal
	 */
	public void resetAnimalState() {
		animal.resetState();
	}

	/**
	 * Renvoi vrai si l'animal a été puni en faisant une mauvaise action. Renvoie
	 * faux sinon.
	 * 
	 * @return
	 */
	public boolean punish() {
		Behavior bh = this.animal.getBehavior();
		Gauge jauge = bh.getActionGauge();

		boolean choice = false;

		// Only with the animal state, we can know if he is doing something good or bad
		// (no need to check on top of which Item he is)

		if (animal.getStates() == States.BAD_ACTION) {
			jauge.increment();
			choice = true;
		} else if (animal.getStates() == States.GOOD_ACTION) {
			jauge.decrement();
		}

//		System.out.println(choice);
		return choice;

	}

	/**
	 * Renvoi vrai si l'animal a été récompenser en faisant une bonne action.
	 * Renvoie faux sinon.
	 * 
	 * @return
	 */
	public boolean reward() {
		Behavior bh = this.animal.getBehavior();
		Gauge jauge = bh.getActionGauge();

		boolean choice = false;

		// Only with the animal state, we can know if he is doing something good or bad
		// (no need to check on top of which Item he is)
		if (animal.getStates() == States.BAD_ACTION) {
			jauge.decrement();
//			choice = false;
		} else if (animal.getStates() == States.GOOD_ACTION) {
			choice = true;
			jauge.increment();
		}
//		System.out.println(choice);

		return choice;
	}

	public Animal getAnimal() {
		return animal;
	}
}
