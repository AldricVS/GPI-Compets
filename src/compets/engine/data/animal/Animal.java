package compets.engine.data.animal;

import compets.engine.data.map.Position;

public class Animal {
	
	private Behavior behavior;
	private Position position;
	private AnimalState state;
	
	public Animal(Position position) {
		this.position = position;
		this.behavior = new Behavior();
		this.state=AnimalState.NEUTRAL;
	}

	public Behavior getBehavior() {
		return behavior;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public AnimalState getState() {
		return state;
	}

	public void setState(AnimalState animalStates) {
		this.state = animalStates;
	}

	public void resetState() {
		state=AnimalState.NEUTRAL;
	}

	@Override
	public String toString() {
		return "Animal [behavior=" + behavior + ", position=" + position + ", state=" + state + "]";
	}
	
}
