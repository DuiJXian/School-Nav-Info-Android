
import 'package:flutter/material.dart';

@immutable
class AppColors extends ThemeExtension<AppColors> {
  final Color primary;
  final Color info;
  final Color warn;
  final Color err;
  final Color fontPrimary;
  final Color fontSecondary;
  final Color bgScreen;
  final Color bgPrimary;
  final Color bgSecondary;
  final Color greyHeavy;
  final Color greyMedium;
  final Color greyLight;
  final Color onButtonColor;

  const AppColors({
    required this.primary,
    required this.info,
    required this.warn,
    required this.err,
    required this.fontPrimary,
    required this.fontSecondary,
    required this.bgScreen,
    required this.bgPrimary,
    required this.bgSecondary,
    required this.greyHeavy,
    required this.greyMedium,
    required this.greyLight,
    required this.onButtonColor,
  });

  static const light = AppColors(
    primary: Color(0xFF0091EA),
    info: Color(0xff18A058),
    warn: Color(0xffF0A020),
    err: Color(0xFFDE4E4E),
    fontPrimary: Colors.black,
    fontSecondary: Color(0xFF383838),
    bgScreen: Color(0xFFEAEAEA),
    bgPrimary: Colors.white,
    bgSecondary: Color(0xFFF5F5F5),
    greyHeavy: Color(0xFF6C6C6C),
    greyMedium: Color(0xFFBDBDBD),
    greyLight: Color(0xFFF1F2F5),
    onButtonColor: Colors.white,
  );

  static const dark = AppColors(
    primary: Color(0xFF0091EA),
    info: Color(0xff18A058),
    warn: Color(0xffF0A020),
    err: Color(0xFFDE4E4E),
    fontPrimary: Colors.white,
    fontSecondary: Colors.white,
    bgScreen: Color(0xFF464646),
    bgPrimary: Color(0xFF313131),
    bgSecondary: Color(0xFF5C5C5C),
    greyHeavy: Color(0xFFC5C5C5),
    greyMedium: Color(0xFFDCDCDC),
    greyLight: Color(0xFFF3F3F3),
    onButtonColor: Colors.white,
  );

  @override
  AppColors copyWith({
    Color? primary,
    Color? info,
    Color? warn,
    Color? err,
    Color? fontPrimary,
    Color? fontSecondary,
    Color? bgScreen,
    Color? bgPrimary,
    Color? bgSecondary,
    Color? greyHeavy,
    Color? greyMedium,
    Color? greyLight,
    Color? onButtonColor,
  }) {
    return AppColors(
      primary: primary ?? this.primary,
      info: info ?? this.info,
      warn: warn ?? this.warn,
      err: err ?? this.err,
      fontPrimary: fontPrimary ?? this.fontPrimary,
      fontSecondary: fontSecondary ?? this.fontSecondary,
      bgScreen: bgScreen ?? this.bgScreen,
      bgPrimary: bgPrimary ?? this.bgPrimary,
      bgSecondary: bgSecondary ?? this.bgSecondary,
      greyHeavy: greyHeavy ?? this.greyHeavy,
      greyMedium: greyMedium ?? this.greyMedium,
      greyLight: greyLight ?? this.greyLight,
      onButtonColor: onButtonColor ?? this.onButtonColor,
    );
  }

  @override
  AppColors lerp(ThemeExtension<AppColors>? other, double t) {
    if (other is! AppColors) return this;
    return AppColors(
      primary: Color.lerp(primary, other.primary, t)!,
      info: Color.lerp(info, other.info, t)!,
      warn: Color.lerp(warn, other.warn, t)!,
      err: Color.lerp(err, other.err, t)!,
      fontPrimary: Color.lerp(fontPrimary, other.fontPrimary, t)!,
      fontSecondary: Color.lerp(fontSecondary, other.fontSecondary, t)!,
      bgScreen: Color.lerp(bgScreen, other.bgScreen, t)!,
      bgPrimary: Color.lerp(bgPrimary, other.bgPrimary, t)!,
      bgSecondary: Color.lerp(bgSecondary, other.bgSecondary, t)!,
      greyHeavy: Color.lerp(greyHeavy, other.greyHeavy, t)!,
      greyMedium: Color.lerp(greyMedium, other.greyMedium, t)!,
      greyLight: Color.lerp(greyLight, other.greyLight, t)!,
      onButtonColor: Color.lerp(onButtonColor, other.onButtonColor, t)!,
    );
  }
}
