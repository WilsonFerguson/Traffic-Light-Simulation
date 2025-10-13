package library.core;

public class PVector {
    public float x, y, z;

    /**
     * Creates a new vector given {@code float} x, {@code float} y, and
     * {@code float} z.
     * 
     * @param x
     * @param y
     * @param z
     */
    public PVector(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    /**
     * Creates a new vector given {@code float} x and {@code float} y.
     * 
     * @param x
     * @param y
     */
    public PVector(double x, double y) {
        this(x, y, 0);
    }

    /**
     * Creates a new (0, 0, 0) vector.
     */
    public PVector() {
        this(0, 0, 0);
    }

    /**
     * Subtracts a {@code PVector} p.
     * 
     * @param vector
     */
    public PVector sub(PVector vector) {
        x -= vector.x;
        y -= vector.y;
        z -= vector.z;
        return this;
    }

    /**
     * Subtracts a {@code float} x, {@code float} y, and {@code float} z.
     * 
     * @param x
     * @param y
     * @param z
     */
    public PVector sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    /**
     * Subtracts a {@code float} x and {@code float} y.
     * 
     * @param x
     * @param y
     */
    public PVector sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    /**
     * Adds a {@code PVector} p.
     * 
     * @param vector
     */
    public PVector add(PVector vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
        return this;
    }

    /**
     * Adds a {@code float} x, {@code float} y, and {@code float} z.
     * 
     * @param x
     * @param y
     * @param z
     */
    public PVector add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * Adds a {@code float} x and {@code float} y.
     * 
     * @param x
     * @param y
     */
    public PVector add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * Multiplies a {@code float} scalar.
     * 
     * @param scalar
     */
    public PVector mult(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * Divides a {@code float} divisor.
     * 
     * @param divisor
     */
    public PVector div(double divisor) {
        x /= divisor;
        y /= divisor;
        z /= divisor;
        return this;
    }

    /**
     * Normalizes the {@code PVector} p.
     * 
     */
    public PVector normalize() {
        float length = MathHelper.sqrt(x * x + y * y + z * z);
        if (length != 0.0)
            div(length);

        return this;
    }

    /**
     * Sets the magnitude to {@code float} magnitude.
     * 
     * @param magnitude
     */
    public PVector setMag(double magnitude) {
        normalize();
        mult(magnitude);
        return this;

    }

    /**
     * Returns the magnitude of the vector as a {@code float}.
     * 
     * @return float
     */
    public float mag() {
        return MathHelper.sqrt(x * x + y * y + z * z);
    }

    /**
     * Returns the squared magnitude of the vector as a {@code float}.
     * 
     * @return float
     */
    public float magSq() {
        return (x * x + y * y + z * z);
    }

    /**
     * Returns the angle of the vector as a {@code float}. (2D only)
     * 
     * @return float
     */
    public float heading() {
        // Returns the angle of the vector
        return MathHelper.atan2(y, x);
    }

    /**
     * Returns the vector where every component is the absolute value of that
     * component.
     * 
     * @return PVector
     */
    public PVector abs() {
        return new PVector(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    /**
     * Sets the heading of the vector to {@code float} angle. (2D only)
     * 
     * @param angle
     */
    public PVector setHeading(double angle) {
        float mag = mag();
        x = (float) (mag * Math.cos(angle));
        y = (float) (mag * Math.sin(angle));
        return this;
    }

    /**
     * Rotate the vector by {@code float} angle. (2D only)
     * 
     * @param angle
     */
    public PVector rotate(double angle) {
        float x = this.x;
        float y = this.y;

        this.x = (float) (x * Math.cos(angle) - y * Math.sin(angle));
        this.y = (float) (x * Math.sin(angle) + y * Math.cos(angle));

        return this;
    }

    /**
     * Returns the distance between this vector and {@code PVector} p.
     * 
     * @param p
     * @return float
     */
    public float dist(PVector vector) {
        float dx = x - vector.x;
        float dy = y - vector.y;
        float dz = z - vector.z;
        return MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Returns the distance between this vector and {@code float} x, {@code float}
     * y, and {@code float} z.
     * 
     * @param x
     * @param y
     * @param z
     * @return float
     */
    public float dist(double x, double y, double z) {
        float dx = this.x - (float) x;
        float dy = this.y - (float) y;
        float dz = this.z - (float) z;
        return MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Returns the distance between this vector and {@code float} x and
     * {@code float} y.
     * 
     * @param x
     * @param y
     * @return float
     */
    public float dist(double x, double y) {
        return (float) Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
    }

    /**
     * Returns the squared distance between this vector and {@code PVector} p.
     * 
     * @param p
     * @return float
     */
    public float distSq(PVector vector) {
        float dx = x - vector.x;
        float dy = y - vector.y;
        float dz = z - vector.z;
        return (dx * dx + dy * dy + dz * dz);
    }

    /**
     * Returns the squared distance between this vector and {@code float} x,
     * {@code float} y, and {@code float} z.
     * 
     * @param x
     * @param y
     * @param z
     * @return float
     */
    public float distSq(double x, double y, double z) {
        float dx = this.x - (float) x;
        float dy = this.y - (float) y;
        float dz = this.z - (float) z;
        return (dx * dx + dy * dy + dz * dz);
    }

    /**
     * Returns the squared distance between this vector and {@code float} x and
     * {@code float} y.
     * 
     * @param x
     * @param y
     * @return float
     */
    public float distSq(double x, double y) {
        return (float) Math.pow(x - this.x, 2) + (float) Math.pow(y - this.y, 2);
    }

    /**
     * Limits the vector to {@code float} max.
     * 
     * @param max
     */
    public PVector limit(double max) {
        if (magSq() > max * max) {
            normalize();
            mult(max);
        }

        return this;
    }

    /**
     * Returns the dot product of this vector and {@code PVector} p.
     * 
     * @param vector
     * @return float
     */
    public float dot(PVector vector) {
        return x * vector.x + y * vector.y + z * vector.z;
    }

    /**
     * Returns the dot product of this vector and {@code float} x, {@code float}
     * y, and {@code float} z.
     * 
     * @param x
     * @param y
     * @param z
     * @return float
     */
    public float dot(double x, double y, double z) {
        return (float) (this.x * x + this.y * y + this.z * z);
    }

    /**
     * Returns the dot product of this vector and {@code float} x and {@code float}
     * y.
     * 
     * @param x
     * @param y
     * @return float
     */
    public float dot(double x, double y) {
        return (float) (this.x * x + this.y * y);
    }

    /**
     * Returns the cross product of this vector and {@code PVector} p.
     * 
     * @param vector
     * @return
     */
    public float cross(PVector vector) {
        return x * vector.y - y * vector.x;
    }

    /**
     * Returns the cross product of this vector and {@code float} x and
     * {@code float} y.
     * 
     * @param x
     * @param y
     * @return float
     */
    public float cross(double x, double y) {
        return (float) (this.x * y - this.y * x);
    }

    /**
     * Linear interpolation the vector to {@code PVector} p by {@code float} amount;
     * 
     * @param vector
     * @param amount
     */
    public PVector lerp(PVector vector, double amount) {
        x += (vector.x - x) * amount;
        y += (vector.y - y) * amount;
        z += (vector.z - z) * amount;

        return this;
    }

    /**
     * Linear interpolation the vector to {@code float} x, {@code float} y, and
     * {@code float} z by {@code float} amount;
     * 
     * @param x
     * @param y
     * @param z
     * @param amount
     */
    public PVector lerp(double x, double y, double z, double amount) {
        this.x += (x - this.x) * amount;
        this.y += (y - this.y) * amount;
        this.z += (z - this.z) * amount;

        return this;
    }

    /**
     * Linear interpolation the vector to {@code float} x and {@code float} y by
     * {@code float} amount;
     * 
     * @param x
     * @param y
     * @param amount
     */
    public PVector lerp(double x, double y, double amount) {
        this.x += (x - this.x) * amount;
        this.y += (y - this.y) * amount;

        return this;
    }

    /**
     * Returns a {@code PVector} copy of this vector.
     * 
     * @return PVector
     */
    public PVector copy() {
        return new PVector(x, y, z);
    }

    /**
     * Returns a {@code float[]} array of this vector.
     * 
     * @return float[]
     */
    public float[] array() {
        return new float[] { x, y, z };
    }

    /**
     * Returns if a {@code Object} is equal to this vector.
     * 
     * @param vector
     * @return boolean
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof PVector) {
            PVector vector = (PVector) object;
            return x == vector.x && y == vector.y && z == vector.z;
        }

        return false;
    }

    /**
     * Returns a hash code for this vector.
     * 
     * @return int
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + Float.floatToIntBits(z);
        return result;
    }

    /**
     * Sets the x, y, and z coordinates of the vector to {@code float} x,
     * {@code float} y, and {@code float} z.
     * 
     * @param x
     * @param y
     * @param z
     */
    public PVector set(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;

        return this;
    }

    /**
     * Sets the x and y of the vector to {@code float} x and {@code float} y.
     * 
     * @param x
     * @param y
     */
    public PVector set(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;

        return this;
    }

    /**
     * Sets the x and y of the vector to {@code PVector} p. Note: processing does
     * not copy the vector, but this library does.
     * 
     * @param vector
     * @return
     */
    public PVector set(PVector vector) {
        x = vector.copy().x;
        y = vector.copy().y;
        z = vector.copy().z;

        return this;
    }

    /**
     * Returns {@code String} representation of this vector.
     * 
     * @return String
     */
    public String toString() {
        if (z == 0) {
            return "(" + x + ", " + y + ")";
        }

        return "(" + x + ", " + y + ", " + z + ")";
    }

    /**
     * Static method to add {@code PVector} vector1 and {@code PVector} vector2.
     * 
     * @param vector1
     * @param vector2
     * @return PVector
     */
    public static PVector add(PVector vector1, PVector vector2) {
        PVector p1 = vector1.copy();
        PVector p2 = vector2.copy();
        p1.add(p2);
        return p1;

    }

    /**
     * Static method to subtract {@code PVector} vector1 and {@code PVector}
     * vector2.
     * 
     * @param vector1
     * @param vector2
     * @return PVector
     */
    public static PVector sub(PVector vector1, PVector vector2) {
        PVector p1 = vector1.copy();
        PVector p2 = vector2.copy();
        p1.sub(p2);
        return p1;
    }

    /**
     * Static method to multiply {@code PVector} vector by {@code float} scalar
     * 
     * @param vector
     * @param scalar
     * @return PVector
     */
    public static PVector mult(PVector vector, double scalar) {
        PVector p = vector.copy();
        p.mult(scalar);
        return p;
    }

    /**
     * Static method to divide {@code PVector} vector by {@code float} divisor.
     * 
     * @param vector
     * @param divisor
     * @return PVector
     */
    public static PVector div(PVector vector, double divisor) {
        PVector p = vector.copy();
        p.div(divisor);
        return p;
    }

    /**
     * Static method to normalize a given {@code PVector} vector.
     * 
     * @param vector
     * @return PVector
     */
    public static PVector normalize(PVector vector) {
        PVector p = vector.copy();
        p.normalize();
        return p;
    }

    /**
     * Static method to return the {@code float} distance between {@code PVector}
     * vector1 and {@code PVector} vector2.
     * 
     * @param vector1
     * @param vector2
     * @return float
     */
    public static float dist(PVector vector1, PVector vector2) {
        PVector p1 = vector1.copy();
        PVector p2 = vector2.copy();
        return p1.dist(p2);
    }

    /**
     * Static method to return the {@code float} distance between {@code float}
     * x1, {@code float} y1 and {@code float} x2, {@code float} y2.
     * {@link PVector#dist(float, float)}.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return float
     */
    public static float dist(double x1, double y1, double x2, double y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Static method to return the {@code float} distance between {@code float}
     * x1, {@code float} y1, {@code float} z1 and {@code float} x2, {@code float}
     * y2, {@code float} z2.
     * {@link PVector#dist(float, float, float)}.
     * 
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return float
     */
    public static float dist(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    /**
     * Static method to return the {@code float} squared distance between
     * {@code PVector} vector1 and {@code PVector} vector2.
     * 
     * @param vector1
     * @param vector2
     * @return float
     */
    public static float distSq(PVector vector1, PVector vector2) {
        PVector p1 = vector1.copy();
        PVector p2 = vector2.copy();
        return p1.distSq(p2);
    }

    /**
     * Static method to return the {@code float} squared distance between
     * {@code float} x1, {@code float} y1 and {@code float} x2, {@code float} y2.
     * {@link PVector#distSq(float, float)}.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return float
     */
    public static float distSq(double x1, double y1, double x2, double y2) {
        return (float) (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Static method to return the {@code float} squared distance between
     * {@code float} x1, {@code float} y1, {@code float} z1 and {@code float} x2,
     * {@code float} y2, {@code float} z2.
     * {@link PVector#distSq(float, float, float)}.
     * 
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return float
     */
    public static float distSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (float) (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    /**
     * Returns a {@code PVector} that is limited to the given {@code float} max. See
     * {@link PVector#limit(float)}.
     * 
     * @param vector
     * @param max
     */
    public static PVector limit(PVector vector, double max) {
        PVector p = vector.copy();
        p.limit(max);
        return p;
    }

    /**
     * Static method to return the {@code float} dot product of {@code PVector}
     * vector1 and {@code PVector} vector2.
     * 
     * @param vector1
     * @param vector2
     * @return float
     */
    public static float dot(PVector vector1, PVector vector2) {
        PVector p1 = vector1.copy();
        PVector p2 = vector2.copy();
        return p1.dot(p2);
    }

    /**
     * Static method to return the {@code float} cross product of {@code PVector}
     * vector1 and {@code PVector} vector2.
     * 
     * @param vector1
     * @param vector2
     * @return float
     */
    public static float cross(PVector vector1, PVector vector2) {
        PVector p1 = vector1.copy();
        PVector p2 = vector2.copy();
        return p1.cross(p2);
    }

    /**
     * Linear interpolation between {@code PVector} vector1 and {@code PVector}
     * vector2
     * 
     * @param vector1
     * @param vector2
     * @param amt
     * @return PVector
     */
    public static PVector lerp(PVector vector1, PVector vector2, double amt) {
        PVector p1 = vector1.copy();
        PVector p2 = vector2.copy();
        p1.lerp(p2, amt);
        return p1;
    }

    /**
     * Calculates and returns the {@code float} angle (in radians) between two
     * vectors.
     * 
     * @param vector1
     * @param vector2
     * @return float
     */
    public static float angleBetween(PVector vector1, PVector vector2) {
        PVector p1 = vector1.copy();
        PVector p2 = vector2.copy();

        float dot = p1.dot(p2);
        float angle = MathHelper.acos(dot / (p1.mag() * p2.mag()));
        return angle;
    }

    /**
     * Returns a new PVector(0, 0).
     * 
     * @return PVector
     */
    public static PVector zero() {
        return new PVector(0, 0, 0);
    }

    /**
     * Returns a new random 2D PVector.
     * 
     * @return PVector
     */
    public static PVector random2D() {
        float angle = MathHelper.random(PConstants.TWO_PI);
        return PVector.fromAngle(angle);
    }

    /**
     * Returns a new random 3D PVector.
     * 
     * @return PVector
     */
    public static PVector random3D() {
        float angle = MathHelper.random(PConstants.TWO_PI);
        float vz = MathHelper.random(-1, 1);
        float vx = MathHelper.sqrt(1 - vz * vz) * MathHelper.cos(angle);
        float vy = MathHelper.sqrt(1 - vz * vz) * MathHelper.sin(angle);
        return new PVector(vx, vy, vz);
    }

    /**
     * Returns a new 2D vector in a random position.
     * 
     * @return PVector
     */
    public static PVector randomPosition() {
        return new PVector(MathHelper.random(0, PComponent.width), MathHelper.random(0, PComponent.height));
    }

    /**
     * Returns a new 2D vector in the center of the screen.
     * 
     * @return PVector
     */
    public static PVector center() {
        return new PVector(PComponent.width / 2, PComponent.height / 2);
    }

    /**
     * Returns a new PVector from the given {@code float} angle. (2D only)
     * 
     * @param angle
     * @return PVector
     */
    public static PVector fromAngle(double angle) {
        return new PVector(MathHelper.cos(angle), MathHelper.sin(angle));
    }
}
