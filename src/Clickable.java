
// clickables is just a thing that user can click / interact with
/*
 * [!] to inspect
 * [v] to go
 * [<] [>] to change angles
 *
*/
class Clickable {

    // -- FIELDS

    String clickableType;   // "turn_left", "turn_right", "dialog", "go"
    String symbol;
    float x, y, w, h; // this is for placement; x,y is the position, w,h is the width and height of clickable drawn

    // type-specific fields
    Integer targetAngle;    // Integer so "missing" differs from 0 (cuz thats important for us). this only applies to "turn_thing" type
    String targetLocation; // only exists on "go" type, will say which location to go to.
    String dialogue; // only really matters im clickable type is dialog. otherwise its generated as null and we dont really care

    // -- METHODS

    @Override
    public String toString() {
        return "{" + clickableType + " '" + symbol + "' x=" + x + " y=" + y
                + " w=" + w + " h=" + h
                + (targetAngle != null ? " targetAngle=" + targetAngle : "")
                + (targetLocation != null ? " targetLocation=" + targetLocation : "")
                + (dialogue != null ? " dialogue=\"" + dialogue + "\"" : "")
                + "}";
    }
}