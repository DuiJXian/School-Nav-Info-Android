class StringUtils {
  static String limitStrLength(
    String str,
    int maxLength, {
    String endStr = '...',
  }) {
    return str.length <= maxLength
        ? str
        : "${str.substring(0, maxLength)}$endStr";
  }
}
