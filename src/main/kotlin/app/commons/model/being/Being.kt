package app.commons.model.being

import app.commons.model.NamedItem
import java.util.UUID

abstract class Being(id: UUID? = null, name: String? = null): NamedItem(id, name)
