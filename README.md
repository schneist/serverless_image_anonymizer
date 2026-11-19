# serverless_image_anonymizer – face anonymization as composable, typed processing steps

This project detects faces in an image with the TensorFlow.js
[BlazeFace](https://github.com/tensorflow/tfjs-models/tree/master/blazeface) model and blurs
them. Each stage of the pipeline is a small, typed, independently testable `Action`, and the
pipeline is built by composing those actions.

The project grew out of work at the AWS Cloud Innovation Center (CIC) in Munich. There,
public-sector prototypes regularly needed images with the people in them made unrecognisable
before any further processing.

## Why

Anonymization is a privacy control, so it has to fail loudly and predictably. The design
follows from that:

- **Every step is an `Action[Environment, Input, Output, Error]`.** Its result is an
  `EitherT[IO, Error, Output]`. Failures are typed domain errors, such as `FileNotFound`,
  `FaceNotFound` or `TFModelNotFound`, not exceptions.
- **Steps compose.** A for-comprehension chains them into a pipeline, and each step can be
  tested on its own.
- **Runtime-agnostic core.** Storage is abstracted behind `FileDescriptorStorage`, and
  `nodefs` provides the Node.js implementation. The long-term goal is a DSL that composes
  steps and plans their execution based on the resources registered in the environment,
  for example a local runtime, a container or a serverless function.

## Pipeline

```scala
for {
  bytes    <- LoadImageAction.execute("images/stefan.png")
  tensor   <- ExtractImageDataAction.execute(bytes)
  model    <- ModelLoadAction.execute(())
  faces    <- EstimateFacesAction.execute((tensor, model))
  image    <- CreateSharpAction.execute(bytes)
  boxes    <- BoundingBoxAction.execute(faces)
  blurred  <- BlurAction.execute((image, boxes))
  png      <- SharpToPNGBufferAction.execute(blurred)
  _        <- FileWriteAction.execute((png, "images/stefan_anon.png"))
} yield ()
```

The full end-to-end test is in `backend/src/test/scala/action/ActionTest.scala`. The blur
radius is set with the `BLUR_RADIUS` environment variable (default 25).

## Structure

| Module | Content |
|---|---|
| `shared` | Domain model and the storage abstraction |
| `nodefs` | Node.js file-system implementation of the storage abstraction |
| `backend` | Actions: image loading, BlazeFace inference, bounding boxes, blurring with sharp, PNG output |
| `frontend` | scalajs-react UI (file tree). Work in progress. |

## Build and test

You need sbt, Node.js and a JDK. The project uses Scala 3.1, Scala.js, ScalablyTyped, Cats
Effect 3, `@tensorflow/tfjs-node`, `@tensorflow-models/blazeface` and `sharp`.

```
sbt backend/test
```

## Status

This is an exploration prototype. The anonymization pipeline works end to end in the tests.
The frontend and the resource-aware execution planning are not finished.

## License

Apache License 2.0. See [LICENSE](LICENSE).
