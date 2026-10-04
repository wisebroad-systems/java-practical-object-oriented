import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MenuItemTest {

    @Test
    @DisplayName("名前と価格を持つメニュー項目を作れる")
    void createsMenuItem() {
        MenuItem coffee = new MenuItem("コーヒー", 400);

        assertEquals("コーヒー", coffee.getName());
        assertEquals(400, coffee.getPrice());
    }

    @Test
    @DisplayName("価格が0円のメニュー項目を作れる")
    void allowsZeroPrice() {
        MenuItem water = new MenuItem("お冷や", 0);

        assertEquals(0, water.getPrice());
    }

    @Test
    @DisplayName("負の価格のメニュー項目は作れない")
    void rejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new MenuItem("コーヒー", -400));
    }

    @Test
    @DisplayName("名前がnull・空文字・空白だけのメニュー項目は作れない")
    void rejectsInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> new MenuItem(null, 400));
        assertThrows(IllegalArgumentException.class, () -> new MenuItem("", 400));
        assertThrows(IllegalArgumentException.class, () -> new MenuItem("   ", 400));
    }
}
