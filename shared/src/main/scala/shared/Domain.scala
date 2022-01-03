package shared

sealed trait Domain

sealed trait Image extends Domain

sealed trait Action extends Domain

sealed trait Collection extends Domain