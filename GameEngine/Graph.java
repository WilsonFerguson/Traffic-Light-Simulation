package GameEngine;

import java.util.ArrayList;

import library.core.*;

public class Graph extends Interactable {
    /**
     * Percentage of the graph that has been drawn (used to annimate it)
     */
    public float percentDrawn = 1.0f;

    /**
     * Stroke weight of the graph's line
     */
    public float graphStrokeWeight = 2.0f;

    /**
     * Title of the graph
     */
    public String title = "";

    /**
     * X-Axis label
     */
    public String labelXAxis = "";

    /**
     * Y-Axis label
     */
    public String labelYAxis = "";

    /**
     * List of all graphs
     */
    public ArrayList<ArrayList<PVector>> graphs = new ArrayList<ArrayList<PVector>>();

    public ArrayList<PVector[]> intersections = new ArrayList<PVector[]>();

    /**
     * Percentage of the size that is padding between the graph and the edge of the
     * rectangle
     */
    public float paddingPercent = 5.0f;

    private float padding = 0;

    /**
     * If a grid should be drawn or not
     */
    public boolean drawGrid = false;

    /**
     * If the points of the graph should be drawn or not
     */
    public boolean drawPoints = true;

    /**
     * Whether intersections are enabled or not.
     */
    public boolean intersectionsEnabled = true;

    /**
     * Colors of the graphed lines
     */
    public ArrayList<color> lineColors = new ArrayList<color>();

    /**
     * Alpha value of the hover info (circle and text)
     */
    private float hoverInfoAlphaPercentage = 0;
    // private float hoverInfoX = Float.MAX_VALUE;
    private ArrayList<Float> hoverInfoXs = new ArrayList<Float>();

    private float minX = Float.MAX_VALUE;
    private float maxX = Float.MIN_VALUE;
    private float minY = Float.MAX_VALUE;
    private float maxY = Float.MIN_VALUE;
    private int maxPoints = 0;
    private float maxXTextWidth = 0;

    // Defaults
    private static Graph defaultGraph = new Graph(PVector.zero(), new PVector(500, 500), new color(255),
            new color(14, 107, 39),
            new color(0), new color(0), 10, new color(0));

    /**
     * <br>
     * NOTE: Background Color is saved to defaultColor and Axis Color is saved to
     * activeColor
     */
    public Graph(PVector pos, PVector size, color backgroundColor, color hoverColor, color axisColor, color strokeColor,
            double textSize, color textColor) {

        this.pos = pos;
        this.size = size;

        this.defaultColor = backgroundColor;
        this.hoverColor = hoverColor;
        this.activeColor = axisColor;
        this.strokeColor = strokeColor;

        this.textSize = (float) textSize;
        this.textColor = textColor;

        currentColor = defaultColor;
    }

    public Graph(double x, double y, double w, double h, color backgroundColor, color hoverColor, color axisColor,
            color strokeColor, double textSize, color textColor) {
        this(new PVector(x, y), new PVector(w, h), backgroundColor, hoverColor, axisColor, strokeColor, textSize,
                textColor);
    }

    public Graph(PVector pos, PVector size) {
        clone(defaultGraph);
        this.pos = pos;
        this.size = size;
    }

    public Graph(double x, double y, double w, double h) {
        this(new PVector(x, y), new PVector(w, h));
    }

    public Graph(PVector pos) {
        clone(defaultGraph);
        this.pos = pos;
    }

    public Graph(double x, double y) {
        this(new PVector(x, y));
    }

    public Graph copy() {
        Graph copy = new Graph(pos.copy(), size.copy(), defaultColor.copy(), hoverColor.copy(), activeColor.copy(),
                strokeColor.copy(), textSize, textColor.copy());

        copy.lineColors = new ArrayList<color>();
        for (color c : lineColors) {
            copy.lineColors.add(c.copy());
        }

        copy.onHover(onHover);
        copy.onHoverExit(onHoverExit);
        copy.setCornerRadius(cornerRadius);
        copy.setStrokeWeight(strokeWeight);
        copy.setInteractive(interactive);
        copy.setActive(active);
        copy.setTextAlignment(textAlignment);
        copy.setAlpha(alpha);
        copy.setPercentDrawn(percentDrawn);
        copy.setGraphStrokeWeight(graphStrokeWeight);
        copy.setGraphTitle(new String(title));
        copy.setLabelXAxis(new String(labelXAxis));
        copy.setLabelYAxis(new String(labelYAxis));

        // TODO: deep copy all of these
        copy.graphs = new ArrayList<ArrayList<PVector>>(graphs);
        copy.intersections = new ArrayList<PVector[]>(intersections);
        copy.lineColors = new ArrayList<color>(lineColors);
        copy.hoverInfoXs = new ArrayList<Float>(hoverInfoXs);
        copy.setDrawGrid(drawGrid);
        copy.setDrawPoints(drawPoints);
        copy.setIntersectionsEnabled(intersectionsEnabled);
        copy.setPaddingPercent(paddingPercent);

        copy.hoverInfoAlphaPercentage = hoverInfoAlphaPercentage;
        copy.minX = minX;
        copy.maxX = maxX;
        copy.minY = minY;
        copy.maxY = maxY;
        copy.maxPoints = maxPoints;
        copy.maxXTextWidth = maxXTextWidth;

        return copy;
    }

    public void clone(Graph graph) {
        pos = graph.pos.copy();
        size = graph.size.copy();
        defaultColor = graph.defaultColor.copy();
        hoverColor = graph.hoverColor.copy();
        activeColor = graph.activeColor.copy();
        strokeColor = graph.strokeColor.copy();
        lineColors = new ArrayList<color>();
        for (color c : graph.lineColors) {
            lineColors.add(c.copy());
        }
        textSize = graph.textSize;
        textColor = graph.textColor.copy();
        onHover = graph.onHover;
        onHoverExit = graph.onHoverExit;
        cornerRadius = graph.cornerRadius;
        interactive = graph.interactive;
        active = graph.active;
        textAlignment = graph.textAlignment;
        percentDrawn = graph.percentDrawn;
        graphStrokeWeight = graph.graphStrokeWeight;
        title = new String(graph.title);
        labelXAxis = new String(graph.labelXAxis);
        labelYAxis = new String(graph.labelYAxis);
        graphs = new ArrayList<ArrayList<PVector>>(graph.graphs);
        intersections = new ArrayList<PVector[]>(graph.intersections);
        drawGrid = graph.drawGrid;
        drawPoints = graph.drawPoints;
        intersectionsEnabled = graph.intersectionsEnabled;
        paddingPercent = graph.paddingPercent;

        currentColor = defaultColor;
    }

    public static void setDefaults(Graph defaults) {
        defaultGraph = defaults;
    }

    public static Graph getDefaults() {
        return defaultGraph;
    }

    public static void setDefaults(PVector pos, PVector size, color backgroundColor, color lineColor, color axisColor,
            color strokeColor, double textSize, color textColor) {
        defaultGraph = new Graph(pos, size, backgroundColor, lineColor, axisColor, strokeColor, textSize, textColor);
    }

    public Graph setCornerRadius(double radius) {
        this.cornerRadius = (int) radius;
        return this;
    }

    public Graph setPercentDrawn(double percent) {
        percentDrawn = (float) percent;
        return this;
    }

    public Graph setGraphStrokeWeight(double weight) {
        graphStrokeWeight = (float) weight;
        return this;
    }

    public Graph setGraphTitle(String title) {
        this.title = title;
        return this;
    }

    public Graph setLabelXAxis(String label) {
        labelXAxis = label;
        return this;
    }

    public Graph setLabelYAxis(String label) {
        labelYAxis = label;
        return this;
    }

    /**
     * Sets whether or not a grid should be drawn
     */
    public Graph setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
        return this;
    }

    /**
     * Sets whether or not the points of the graph should be drawn
     */
    public Graph setDrawPoints(boolean drawPoints) {
        this.drawPoints = drawPoints;
        return this;
    }

    /**
     * Sets whether or not intersections should be enabled (snapping to them and
     * drawing them on the graph)
     */
    public Graph setIntersectionsEnabled(boolean intersectionsEnabled) {
        this.intersectionsEnabled = intersectionsEnabled;
        return this;
    }

    /**
     * Sets the percentage of the size that is padding between the graph and the
     * edge of the rectangle. <br>
     * Note: 5% is "5.0", 10% is "10.0", etc.
     */
    public Graph setPaddingPercent(double paddingPercent) {
        this.paddingPercent = (float) paddingPercent;
        return this;
    }

    public float getPercentDrawn() {
        return percentDrawn;
    }

    public float getGraphStrokeWeight() {
        return graphStrokeWeight;
    }

    public String getGraphTitle() {
        return title;
    }

    public String getLabelXAxis() {
        return labelXAxis;
    }

    public String getLabelYAxis() {
        return labelYAxis;
    }

    public boolean getDrawGrid() {
        return drawGrid;
    }

    public boolean getDrawPoints() {
        return drawPoints;
    }

    public boolean getIntersectionsEnabled() {
        return intersectionsEnabled;
    }

    public float getPaddingPercent() {
        return paddingPercent;
    }

    public Graph clearGraphs() {
        graphs.clear();
        intersections.clear();
        lineColors.clear();
        hoverInfoXs.clear();
        minX = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        maxY = Float.MIN_VALUE;
        maxPoints = 0;
        maxXTextWidth = 0;
        return this;
    }

    public Graph addGraph(ArrayList<PVector> graph) {
        graphs.add(graph);
        return this;
    }

    /**
     * Begins a new graph with a random color
     */
    public Graph newGraph() {
        graphs.add(new ArrayList<PVector>());
        lineColors.add(color.randomColor());
        hoverInfoXs.add(Float.MAX_VALUE);
        return this;
    }

    /**
     * Begins a new graph with a specified color
     */
    public Graph newGraph(color lineColor) {
        graphs.add(new ArrayList<PVector>());
        lineColors.add(lineColor);
        hoverInfoXs.add(Float.MAX_VALUE);
        return this;
    }

    public Graph addPoint(PVector point) {
        PVector p = point.copy();
        if (graphs.size() == 0)
            newGraph();
        graphs.get(graphs.size() - 1).add(p);

        // Sort graph by x value
        graphs.get(graphs.size() - 1).sort((a, b) -> Float.compare(a.x, b.x));

        if (p.x < minX)
            minX = p.x;
        if (p.x > maxX)
            maxX = p.x;
        if (p.y < minY)
            minY = p.y;
        if (p.y > maxY)
            maxY = p.y;

        int size = graphs.get(graphs.size() - 1).size();
        if (size > maxPoints)
            maxPoints = size;

        push();
        textSize(textSize);
        float w = textWidth(String.format("%.2f", p.x));
        if (w > maxXTextWidth)
            maxXTextWidth = w;
        pop();

        calculateIntersections();

        return this;
    }

    public Graph addPoint(double x, double y) {
        return addPoint(new PVector(x, y));
    }

    /**
     * Returns an ArrayList of intersections. Each element in the ArrayList is a
     * PVector array of size 2. The first element is the point of intersection, the
     * second element is the indices in the graphs list of the two graphs that
     * intersects
     */
    public ArrayList<PVector[]> getIntersections() {
        return intersections;
    }

    public void draw() {
        if (!active)
            return;

        boolean previouslyHovered = isHovered;
        isHovered = hover();
        if (!previouslyHovered && isHovered)
            onHover();
        else if (previouslyHovered && !isHovered)
            onHoverExit();

        padding = min(size.x, size.y) * paddingPercent / 100;

        drawBackground();
        drawGraph();
        drawMouseHover();
    }

    /**
     * Bottom left point of the axes
     */
    private PVector axesBottomLeft() {
        float x = pos.x - size.x / 2;
        x += padding;
        x += maxXTextWidth; // Width of label
        x += 3; // Margin between label and axis

        float y = pos.y + size.y / 2;
        y -= padding;
        push();
        textSize(textSize);
        y -= textAscent(); // Height of label
        pop();

        return new PVector(x, y);
    }

    /**
     * Top right point of the axes (Note, no axis will intersect this point, it's
     * just x/y values)
     */
    private PVector axesTopRight() {
        float x = pos.x + size.x / 2;
        x -= padding;

        float y = pos.y - size.y / 2;
        y += padding;

        return new PVector(x, y);
    }

    /**
     * Returns the bottom left point of the line graph part
     */
    private PVector graphStartPoint() {
        float x = axesBottomLeft().x;
        x += padding / 2;

        float y = axesBottomLeft().y;
        y -= padding;
        y -= padding / 2;

        return new PVector(x, y);
    }

    /**
     * Returns the top right point of the line graph part
     */
    private PVector graphEndPoint() {
        float x = pos.x + size.x / 2;
        x -= padding;
        x -= padding / 2;

        float y = pos.y - size.y / 2;
        y += padding;
        y += padding / 2;

        return new PVector(x, y);
    }

    private void drawBackground() {
        currentColor = lerpColor(currentColor, defaultColor, Animator.colorLerpAmount);

        // Canvas
        stroke(strokeColor, strokeColor.a * (alpha / 255.0f));
        strokeWeight(strokeWeight);
        rectMode(CENTER);
        fill(currentColor, currentColor.a * (alpha / 255.0f));
        rect(pos, size, cornerRadius);

        PVector start = graphStartPoint();
        PVector end = graphEndPoint();

        int numXLabels = maxPoints;
        float xStep = (end.x - start.x) / numXLabels;

        // TODO: Maybe change how step is being calculated so that it optimizes for not
        // overlapping labels rather than percents

        // If xStep is less than 10% of the usable width, then figure out how many x's
        // should be labeled such that xStep = 10% of the width
        if (xStep < (end.x - start.x) * 0.1) {
            numXLabels = 10; // Turns out it's always just 10 (size cancels out)
            xStep = (end.x - start.x) / numXLabels;
        }

        int numYLabels = maxPoints;
        float yStep = abs((end.y - start.y) / numYLabels);
        if (yStep < abs((end.y - start.y) * 0.1)) {
            numYLabels = 10;
            yStep = abs((end.y - start.y) / numYLabels);
        }

        // Because of fencepost problem, the number of labels is one more than the
        // number (but we won't change the variable)

        // Grid
        PVector axesBottomLeft = axesBottomLeft();
        PVector axesTopRight = axesTopRight();
        if (drawGrid) {
            // Without fancy alpha stuff, this was 100
            // stroke(activeColor, 100); // activeColor is axes color
            // Now:
            float alphaPercentage = (activeColor.a / 255.0f) * alpha / 255.0f;
            stroke(activeColor, alphaPercentage * 100.0f);
            strokeWeight(1.5);

            // Vertical lines
            for (int i = 0; i <= numXLabels; i++) {
                float x = start.x + i * xStep;
                line(x, axesBottomLeft.y, x, axesTopRight.y);
            }

            // Horizontal lines
            for (int i = 0; i <= numYLabels; i++) {
                float y = start.y - i * yStep;
                line(axesBottomLeft.x, y, axesTopRight.x, y);
            }
        }

        // Axes
        stroke(activeColor, activeColor.a * (alpha / 255.0f));
        strokeWeight(2);
        line(axesBottomLeft.x, axesBottomLeft.y, axesTopRight.x, axesBottomLeft.y);
        line(axesBottomLeft.x, axesBottomLeft.y, axesBottomLeft.x, axesTopRight.y);

        // Labels
        // TODO: Dynamically change text size based on how many labels or maybe size of
        // graph
        textSize(textSize);
        fill(textColor, textColor.a * (alpha / 255.0f));

        // Y labels
        textAlign(TextAlignment.RIGHT);
        for (int i = 0; i <= numYLabels; i++) {
            float y = start.y - i * yStep;
            float value = map(i, 0, numYLabels, minY, maxY);
            text(String.format("%.2f", value), axesBottomLeft.x - 3, y);
        }

        // X labels
        textAlign(TextAlignment.CENTER);
        float y = axesBottomLeft.y + textAscent();
        for (int i = 0; i <= numXLabels; i++) {
            float x = start.x + i * xStep;
            float value = map(i, 0, numXLabels, minX, maxX);
            text(String.format("%.2f", value), x, y);
        }

        // Title + labels text
        textSize(size.y * paddingPercent / 100 / 2);
        textAlign(TextAlignment.CENTER);
        text(title, pos.x, pos.y - size.y / 2 + textAscent() * 1.05);

        textSize(padding / 2 * 0.8f);
        text(labelXAxis, (start.x + end.x) / 2, pos.y + size.y / 2 - textAscent() * 1.05);
        push();
        translate(pos.x - size.x / 2 + textAscent() * 1.05, (start.y + end.y) / 2);
        rotate(-HALF_PI);
        text(labelYAxis, 0, 0);
        pop();
    }

    private float getMaxXToDraw(ArrayList<PVector> graph) {
        return map(constrain(percentDrawn, 0, 1), 0, 1, graph.get(0).x, graph.get(graph.size() - 1).x);
    }

    private void drawGraph() {
        if (percentDrawn == 0.0f)
            return;

        PVector start = graphStartPoint();
        PVector end = graphEndPoint();
        for (int i = 0; i < graphs.size(); i++) {
            ArrayList<PVector> graph = graphs.get(i);
            if (graph.size() == 0)
                continue;

            float maxXToDraw = getMaxXToDraw(graph);

            stroke(lineColors.get(i), lineColors.get(i).a * (alpha / 255.0f));
            strokeWeight(graphStrokeWeight);
            noFill();

            beginShape();

            for (int j = 0; j < graph.size(); j++) {
                PVector p = graph.get(j);
                if (p.x > maxXToDraw) {
                    // Draw the line up to this point and then break
                    if (j > 0) {
                        float y = map(approximateY(graph, maxXToDraw), minY, maxY, start.y, end.y);
                        float x = map(maxXToDraw, minX, maxX, start.x, end.x);
                        vertex(x, y);
                    }
                    break;
                }

                float x = map(p.x, minX, maxX, start.x, end.x);
                float y = map(p.y, minY, maxY, start.y, end.y);

                vertex(x, y);
            }

            endShape();

            // Draw points
            if (drawPoints) {
                for (int j = 0; j < graph.size(); j++) {
                    PVector p = graph.get(j);
                    if (p.x > maxXToDraw)
                        break;

                    float x = map(p.x, minX, maxX, start.x, end.x);
                    float y = map(p.y, minY, maxY, start.y, end.y);

                    float brightness = brightness(lineColors.get(i));
                    if (brightness < 128)
                        stroke(lineColors.get(i).copy().setBrightness(brightness + 30),
                                lineColors.get(i).a * (alpha / 255.0f));
                    else
                        stroke(lineColors.get(i).copy().setBrightness(brightness - 30),
                                lineColors.get(i).a * (alpha / 255.0f));

                    strokeWeight(graphStrokeWeight * 1.5);
                    point(x, y);
                }
            }
        }

        if (intersectionsEnabled) {
            for (PVector[] p : intersections) {

                float maxX1 = getMaxXToDraw(graphs.get((int) p[1].x));
                float maxX2 = getMaxXToDraw(graphs.get((int) p[1].y));
                float maxX = min(maxX1, maxX2);

                if (p[0].x > maxX)
                    continue;

                PVector point = graphToCanvas(p[0]);
                color col = lerpColor(lineColors.get((int) p[1].x), lineColors.get((int) p[1].y), 0.5f);

                stroke(col, col.a * (alpha / 255.0f));
                strokeWeight(graphStrokeWeight * 1.1);
                point(point);
            }
        }
    }

    private void calculateIntersections() {
        intersections.clear();
        for (int i = 0; i < graphs.size(); i++) {
            ArrayList<PVector> graph = graphs.get(i);
            for (int j = 0; j < graph.size() - 1; j++) {
                PVector p1 = graph.get(j);
                PVector p2 = graph.get(j + 1);

                for (int k = 0; k < graphs.size(); k++) {
                    if (k == i)
                        continue;

                    ArrayList<PVector> otherGraph = graphs.get(k);
                    for (int l = 0; l < otherGraph.size() - 1; l++) {
                        PVector p3 = otherGraph.get(l);
                        PVector p4 = otherGraph.get(l + 1);

                        PVector intersection = lineLineIntersection(p1, p2, p3, p4);
                        if (intersection != null) {
                            // Check to make sure that intersection isn't already in the list
                            boolean alreadyInList = false;
                            for (PVector[] p : intersections) {
                                if (p[0].dist(intersection) < 0.01) {
                                    alreadyInList = true;
                                    break;
                                }
                            }
                            if (!alreadyInList) {
                                intersection = canvasToGraph(intersection);
                                intersections.add(new PVector[] { intersection, new PVector(min(i, k), max(i, k)) });
                            }
                        }
                    }
                }
            }
        }
    }

    private PVector lineLineIntersection(PVector p1, PVector p2, PVector p3, PVector p4) {
        PVector start = graphStartPoint();
        PVector end = graphEndPoint();

        p1 = graphToCanvas(p1, start, end);
        p2 = graphToCanvas(p2, start, end);
        p3 = graphToCanvas(p3, start, end);
        p4 = graphToCanvas(p4, start, end);

        // Paul Bourke's algorithm
        float denominator = (p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y -
                p1.y);
        if (denominator == 0)
            return null;

        float ua = ((p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x)) /
                denominator;
        float ub = ((p2.x - p1.x) * (p1.y - p3.y) - (p2.y - p1.y) * (p1.x - p3.x)) /
                denominator;

        if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1) {
            float x = p1.x + ua * (p2.x - p1.x);
            float y = p1.y + ua * (p2.y - p1.y);

            return new PVector(x, y);
        }

        return null;
    }

    private PVector canvasToGraph(PVector p) {
        return new PVector(map(p.x, graphStartPoint().x, graphEndPoint().x, minX, maxX),
                map(p.y, graphStartPoint().y, graphEndPoint().y, minY, maxY));
    }

    private PVector graphToCanvas(PVector p) {
        return graphToCanvas(p, graphStartPoint(), graphEndPoint());
    }

    private PVector graphToCanvas(PVector p, PVector start, PVector end) {
        return new PVector(map(p.x, minX, maxX, start.x, end.x), map(p.y, minY, maxY, start.y, end.y));
    }

    private void drawMouseHover() {
        if (!isMouseInsideGraph()) {
            if (hoverInfoAlphaPercentage > 0) {
                hoverInfoAlphaPercentage = lerp(hoverInfoAlphaPercentage, 0, Animator.colorLerpAmount);
                if (hoverInfoAlphaPercentage < 0.01f)
                    hoverInfoAlphaPercentage = 0;
            } else {
                // hoverInfoX = Float.MAX_VALUE;
                for (int i = 0; i < graphs.size(); i++) {
                    hoverInfoXs.set(i, Float.MAX_VALUE);
                }
                return;
            }
        } else {
            if (hoverInfoAlphaPercentage < 1) {
                hoverInfoAlphaPercentage = lerp(hoverInfoAlphaPercentage, 1, Animator.colorLerpAmount);
                if (hoverInfoAlphaPercentage > 0.99f)
                    hoverInfoAlphaPercentage = 1;
            }
        }

        PVector start = graphStartPoint();
        PVector end = graphEndPoint();

        float x = map(mouseX, start.x, end.x, minX, maxX);
        for (int i = 0; i < graphs.size(); i++) {
            if (graphs.get(i).size() == 0)
                continue;
            drawHoverInfo(i, x);
        }
    }

    private boolean isMouseInsideGraph() {
        if (!hover() || !interactive)
            return false;

        return true;
    }

    /**
     * Returns the target location for the hover point. x goes from minX to maxX.
     */
    private PVector getHoverInfoPoint(ArrayList<PVector> graph, float x) {
        PVector start = graphStartPoint();
        PVector end = graphEndPoint();

        // Mouse past the right most point:
        float maxXForGraph = getMaxXToDraw(graph);
        if (percentDrawn == 1.0) {
            if (x > graph.get(graph.size() - 1).x) {
                return graph.get(graph.size() - 1);
            }
        } else {
            if (x > maxXForGraph) {
                float y = approximateY(graph, maxXForGraph);
                return new PVector(maxXForGraph, y);
            }
        }

        // Mouse past the left most point:
        if (x < graph.get(0).x) {
            return graph.get(0);
        }

        // First check to see if it's close to an intersection (if intersections are
        // enabled)
        if (intersectionsEnabled) {
            for (PVector[] p : intersections) {
                float dist = map(p[0].x, minX, maxX, start.x, end.x) - mouseX;
                if (abs(dist) < size.x * 0.01) {
                    return p[0];
                }
            }
        }

        // Then check to see if it's close to a point on the graph
        PVector closest = null;
        float closestDist = Float.MAX_VALUE;
        for (PVector p : graph) {
            float pointX = map(p.x, minX, maxX, start.x, end.x);
            float dist = abs(pointX - mouseX);
            if (dist < closestDist) {
                closest = p;
                closestDist = dist;
            }
        }

        if (closestDist < size.x * 0.01) {
            return closest;
        }

        float y = approximateY(graph, x);
        return new PVector(x, y);
    }

    private void drawHoverInfo(int i, float x) {
        PVector start = graphStartPoint();
        PVector end = graphEndPoint();

        ArrayList<PVector> graph = graphs.get(i);

        PVector target = getHoverInfoPoint(graph, x);
        if (hoverInfoXs.get(i) == Float.MAX_VALUE) {
            hoverInfoXs.set(i, target.x);
        } else {
            hoverInfoXs.set(i, lerp(hoverInfoXs.get(i), target.x, 0.2f));
            if (abs(hoverInfoXs.get(i) - target.x) < 0.001f)
                hoverInfoXs.set(i, target.x);
        }

        float xVal = map(hoverInfoXs.get(i), minX, maxX, start.x, end.x);
        float yVal = map(approximateY(graph, hoverInfoXs.get(i)), minY, maxY, start.y, end.y);

        float radius = size.x * 0.005f;
        noStroke();
        fill(lineColors.get(i), hoverInfoAlphaPercentage * alpha);
        circle(xVal, yVal, radius);

        textSize(textSize);
        textAlign(TextAlignment.CENTER);

        float baseY = yVal - radius * 2 - textDescent();
        float textY = baseY;
        float rectY = baseY;
        String text = String.format("(%.2f, %.2f)", target.x, target.y);

        fill(hoverColor, 125 * hoverInfoAlphaPercentage * (alpha / 255.0));
        rectMode(CENTER);
        rect(xVal, rectY, textWidth(text) * 1.1, (textAscent() + textDescent()) * 1.1, 5);

        fill(textColor, 255 * hoverInfoAlphaPercentage * (alpha / 255.0));
        text(text, xVal, textY);
    }

    private float approximateY(ArrayList<PVector> graph, float x) {
        if (graph.size() == 0)
            return 0;

        if (x < graph.get(0).x)
            return 0;

        if (x > graph.get(graph.size() - 1).x)
            return 0;

        PVector p1 = null;
        PVector p2 = null;
        for (int i = 0; i < graph.size() - 1; i++) {
            if (graph.get(i).x <= x && graph.get(i + 1).x >= x) {
                p1 = graph.get(i);
                p2 = graph.get(i + 1);
                break;
            }
        }

        if (p1 == null || p2 == null)
            return -1;

        float m = (p2.y - p1.y) / (p2.x - p1.x);
        float b = p1.y - m * p1.x;

        return m * x + b;
    }

    public void delete() {
        super.delete();
        graphs.clear();
        intersections.clear();
        lineColors.clear();
        hoverInfoXs.clear();
    }
}
