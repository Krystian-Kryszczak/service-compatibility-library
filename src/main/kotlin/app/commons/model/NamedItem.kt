package app.commons.model

import app.commons.model.Item
import app.commons.model.Named
import java.util.UUID

abstract class NamedItem(id: UUID? = null, override val name: String? = null): Item(id), Named
