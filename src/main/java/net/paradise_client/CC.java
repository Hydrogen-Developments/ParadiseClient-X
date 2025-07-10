package net.paradise_client;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CC {

    private static final String LEGACY_PREFIX = "&";

    /**
     * Converts a string with color codes (&) into a formatted MutableText object.
     *
     * @param input The string containing color codes (&).
     * @return A MutableText object with the applied formats.
     */
    public static MutableText parseColorCodes(String input) {
        MutableText result = Text.empty();
        StringBuilder currentText = new StringBuilder();
        Formatting currentFormatting = Formatting.RESET;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == LEGACY_PREFIX.charAt(0) && i + 1 < input.length()) {
                char codeChar = input.charAt(i + 1);
                Formatting formatting = getFormattingFromCode(codeChar);

                if (formatting != null) {
                    if (!currentText.isEmpty()) {
                        result.append(Text.literal(currentText.toString()).formatted(currentFormatting));
                        currentText.setLength(0);
                    }
                    currentFormatting = formatting;
                    i++;
                } else {
                    currentText.append(c);
                }

            } else {
                currentText.append(c);
            }
        }

        if (!currentText.isEmpty()) {
            result.append(Text.literal(currentText.toString()).formatted(currentFormatting));
        }

        return result;
    }

    /**
     * Gets the format corresponding to a color code (&).
     *
     * @param code The color code (e.g., 'c' for red).
     * @return The corresponding format, or null if not found.
     */
    private static Formatting getFormattingFromCode(char code) {
        return switch (Character.toLowerCase(code)) {
            case '0' -> Formatting.BLACK;
            case '1' -> Formatting.DARK_BLUE;
            case '2' -> Formatting.DARK_GREEN;
            case '3' -> Formatting.DARK_AQUA;
            case '4' -> Formatting.DARK_RED;
            case '5' -> Formatting.DARK_PURPLE;
            case '6' -> Formatting.GOLD;
            case '7' -> Formatting.GRAY;
            case '8' -> Formatting.DARK_GRAY;
            case '9' -> Formatting.BLUE;
            case 'a' -> Formatting.GREEN;
            case 'b' -> Formatting.AQUA;
            case 'c' -> Formatting.RED;
            case 'd' -> Formatting.LIGHT_PURPLE;
            case 'e' -> Formatting.YELLOW;
            case 'f' -> Formatting.WHITE;
            case 'k' -> Formatting.OBFUSCATED;
            case 'l' -> Formatting.BOLD;
            case 'm' -> Formatting.STRIKETHROUGH;
            case 'n' -> Formatting.UNDERLINE;
            case 'o' -> Formatting.ITALIC;
            case 'r' -> Formatting.RESET;
            default -> null;
        };
    }
              }
      
