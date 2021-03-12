package compets.tests.unit.interaction;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import compets.engine.data.animal.Dog;
import compets.engine.data.animal.Gauge;
import compets.engine.data.animal.AnimalState;
import compets.engine.data.map.Map;
import compets.engine.data.map.Position;
import compets.engine.data.map.item.BadItem;
import compets.engine.process.AnimalManager;

/**
 * @author Maxence
 */
public class AnimalInteractBadItem {

	private static Map map;
	private static Position position;
	private static Dog animal;
	private static AnimalManager manager;

	@BeforeClass
	public static void init() {
		map = new Map(1, 1);

		position = new Position(0, 0);
		map.getMap()[position.getX()][position.getY()] = new BadItem(position);

		animal = new Dog(position);
		animal.getBehavior().getActionGauge().setValue(Gauge.MIN_GAUGE);

		manager = new AnimalManager(animal, map);
		manager.interact();
	}
	
	@Test
	public void behaviorChangedByInteraction() {
		assertEquals(AnimalState.BAD_ACTION, animal.getState());
	}

	@Test
	public void dontGetReward() {
		assertFalse(manager.reward());
	}

	@Test
	public void getPunish() {
		assertTrue(manager.punish());
	}
}
