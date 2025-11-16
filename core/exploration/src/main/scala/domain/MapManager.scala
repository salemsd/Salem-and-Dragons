package domain

import errors.MapError
import in.ForValidatingMap

class MapManager() extends ForValidatingMap:
  override def validateAndStoreMap(dataLines: List[String]): Either[MapError, Unit] = ???
