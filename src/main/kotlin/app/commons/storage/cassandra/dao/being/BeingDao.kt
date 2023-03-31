package app.commons.storage.cassandra.dao.being

import app.commons.model.being.Being
import app.commons.storage.cassandra.dao.ItemDao

interface BeingDao<T: Being>: ItemDao<T>
