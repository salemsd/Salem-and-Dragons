package in

import errors.MapError

trait ForValidatingMap:
  def validateAndStoreMap(dataLines: List[String]): Either[MapError, Unit]
