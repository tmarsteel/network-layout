## Stage 1 - Identifying the cornerstone Stations and their relations

### Identifying cornerstones

A run of stations all served by the same 1..n lines is very simple to layout because there's not much choice. Thus, these can be ignored in
the first stage for simplicity and speed. Thus, the first stage consist of finding the "cornerstone" stations; the stations that will be the most difficult to
place on the map because they have many heterogenous connections.

* all stations that are start/destination stations of any line are cornerstone stations (this has other practical reasons, will hopefully become apparent when reading further)
* of the remaining, all stations where these conditions hold are *not* cornerstone stations:
  * the set of incoming and outgoing lines (primary and secondary direction) is identical
  * all incoming lines (primary and secondary) come from the same station
  * all outgoing lines (primary and secondary) go to the same station
* all remaining stations are conerstone stations

### Relations

Form an undirected graph where every cornerstone station is a node. For all stations A and B: if there is a line running from A to B in any direction, A and B have an edge
in the graph.

## Stage 2 - laying out the conerstone stations

With the graph created above we can figure out how to position the cornerstones _relative to each other_ (no absolute locations yet!).

Each station is given 8 geographic directions relative to itself: north, north-east, east, south-east, south, south-west, west, north-west.

We allow the user to define a "gravity" for as many of the cornerstone stations as they wish. The gravity is one of these 8 directions. The goal is that the station ends
up in that direction relative to the map center. This is supposed to keep somewhat of a resemblence between the physical/geographi location of stations and where they're
shown on the abstract map.

With this information available, we can define a CLP-Z problem whichs solution will give us the first very abstract layout of the cornerstone stations:

* every station+edge gets a variable for the direction in which the edge connects to the station (one of the 8 above). These two variables on two stations connected by
  an edge must be constrained so that the directions are compatible:
  * Station A north allows Station B directions: south
  * Station A north-east allows Station B directions: south-west
  * Station A east allows Station B directions: west
  * Station A south-east allows Station B directions: north-west
  * Station A south allows Station B directions: north
  * Station A south-west allows Station B directions: north-east
  * Station A west allows Station B directions: east
  * Station A north-west allows Station B directions: south-east
* every combination of any 2 edges gets a variable denoting whether these edges intersect.
  **TODO: how to define that with only general directions??**

When looking for a solution, we can try and minimize the number of intersections. We could even weigh every edge-intersection
by the number of lines that would intersect to avoid intersecting many lines if at all possible.

**This implies that any cornerstone stations can have at most 8 vertices in the stage-1 graph, otherwise layouting will fail!**

## Stage 3 - distances between cornerstones

## Stage 4 - placement of non-cornerstone stations along the runs between cornerstone stations, including text labels
