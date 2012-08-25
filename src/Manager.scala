class Manager() {
  import Trait._;

  var entities: List[Entity] = List()

  def get(entity_type: Trait): List[Entity] = {
    entities.filter(e => e.traits.contains(entity_type))
  }
  
  def one(entity_type: Trait): Entity = {
    val list: List[Entity] = get(entity_type)
    assert(list.length == 1)

    list(0)
  }

  def add(entity: Entity): Unit = {
    entities = entities :+ entity
  }

  def update_all(): Unit = {
    get(Update).map(_.update(this))
  }

  def draw_all(): Unit = {
    get(Draw).sortBy(_.depth).foreach(_.draw)
  }
}