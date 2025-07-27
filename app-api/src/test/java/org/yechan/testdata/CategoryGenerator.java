package org.yechan.testdata;

import org.yechan.entity.Show.Category;

public class CategoryGenerator {
    public static Category pickAnyCategory() {
        return Category.values()[(int) (Math.random() * Category.values().length)];
    }
}
