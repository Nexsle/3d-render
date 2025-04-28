# 3D Renderer
A Java-based 3D rendering engine that implements basic 3D graphics techniques to render and manipulate a 3D object in real-time.
![3D Renderer Demo](./assets/demo.gif)

## Requirements

Java Development Kit (JDK) 8 or higher
Swing library (included in standard JDK)

Running the Application

Clone the repository
Compile the Java files:
```
javac *.java
```

Run the DemoViewer class:
```
java .\src\DemoViewer.java
```

## Features

Real-time 3D rendering with interactive controls
3D matrix transformations for rotation and perspective
Triangle-based mesh rendering
Z-buffer implementation for correct depth ordering
Barycentric coordinate calculation for triangle filling
Lighting simulation with normal vector calculation
Dynamic mesh subdivision for smooth spherical objects

# Technical Implementation
## Core Components

Matrix3D: Handles 3D matrix operations and transformations
Vertex: Represents 3D coordinates in space
Triangle: Defines triangular faces with vertices and color
DemoViewer: Main rendering engine and UI display

## Rendering Pipeline

Object Definition: Define 3D objects as a collection of triangles
Mesh Refinement: Subdivide triangles to create smoother surfaces
Transformation: Apply rotation matrices to the 3D object
Projection: Convert 3D coordinates to 2D screen space
Rasterization: Fill triangles using barycentric coordinates
Z-buffering: Determine visible pixels based on depth
Lighting: Calculate shading based on surface normals
Display: Render the final image to the screen

## Mathematical Techniques

Matrix Multiplication: For applying transformations
Vector Normalization: For light calculations
Barycentric Coordinates: For triangle rasterization
Normal Vector Calculation: For determining surface orientation

## Controls

Horizontal Slider: Controls rotation around the vertical axis (heading)
Vertical Slider: Controls rotation around the horizontal axis (pitch)


