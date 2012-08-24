object Traits extends Enumeration {
  type Traits = Value
  val Update, Draw, Transparent = Value;
}

class Manager() {
  var entities: List[Entity] = List()

  def get(entity_type: String): List[Entity] = {
    entities.filter(e => e.traits.contains(entity_type))
  }

  def one(entity_type: String): Entity = {
    val list: List[Entity] = get(entity_type)
    assert(list.length == 1)

    list(0)
  }

  def add(entity: Entity): Unit = {
    entities = entities :+ entity
  }

  def update_all(): Unit = {
    get("update").map(_.update(this))
  }

  def draw_all(): Unit = {
    get("draw").sortBy(_.depth).foreach(_.draw)
  }
}