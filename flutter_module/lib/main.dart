import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_module/pages/course_table_page/course_table_page.dart';
import 'package:flutter_module/pages/course_table_page/view_models/course_table_view_model.dart';
import 'package:flutter_module/them/app_colors.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

var logger = Logger();

// final logger = Logger();

void main() => runApp(
  ChangeNotifierProvider(create: (_) => CourseTableViewModel(), child: MyApp()),
);

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        colorScheme: ColorScheme.light(
          primary: Color(0xFF0091EA)
        ),
        brightness: Brightness.light,
        extensions: const <ThemeExtension<dynamic>>[AppColors.light],
      ),
      darkTheme: ThemeData(
        colorScheme: ColorScheme.dark(
          primary: Color(0xFF0091EA)
        ),
        brightness: Brightness.dark,
        extensions: const <ThemeExtension<dynamic>>[AppColors.dark],
      ),
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: const [
        Locale('zh'), // 中文
        Locale('en'), // 英文
      ],
      home: const CourseTablePage(),
    );
  }
}
