package ui

sealed trait AnonymizationErrors

sealed trait GenericErrors extends AnonymizationErrors
case object UnknownError extends GenericErrors

sealed trait ConversionErrors extends AnonymizationErrors

sealed trait LoadImageErrors extends AnonymizationErrors
class FileNotFound extends LoadImageErrors

sealed trait TFErrors extends AnonymizationErrors
case object TFModelNotFound extends TFErrors

sealed trait EstimationErrors extends AnonymizationErrors
case object FaceNotFound extends EstimationErrors

sealed trait SharpErrors extends AnonymizationErrors
case object GenericSharpError extends SharpErrors

