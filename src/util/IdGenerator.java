package util;

import java.util.Collection;

public class IdGenerator {
    private IdGenerator() {
    }

    public static String generateNextId(String prefix, Collection<String> existingIds) {
        int maxNumber = 0;
        String normalizedPrefix = prefix.toUpperCase() + "-";

        for (String id : existingIds) {
            if (id == null || !id.toUpperCase().startsWith(normalizedPrefix)) {
                continue;
            }

            String numberPart = id.substring(normalizedPrefix.length());
            try {
                maxNumber = Math.max(maxNumber, Integer.parseInt(numberPart));
            } catch (NumberFormatException ignored) {
                // Ignore legacy IDs that do not use the numeric format.
            }
        }

        return String.format("%s-%04d", prefix.toUpperCase(), maxNumber + 1);
    }
}
