import 'package:flutter/material.dart';
import 'package:flutter_module/pages/course_table_page/widget/course_table_widget.dart';
import 'package:flutter_module/pages/course_table_page/widget/bind_account_widget.dart';
import 'package:flutter_module/pages/course_table_page/widget/score_widget.dart';
import 'package:flutter_module/pages/course_table_page/view_models/course_table_view_model.dart';
import 'package:provider/provider.dart';

class CourseTablePage extends StatelessWidget {
  const CourseTablePage({super.key});

  @override
  Widget build(BuildContext context) {
    final viewModel = context.watch<CourseTableViewModel>();
    return Scaffold(
      body: Stack(
        children: [
          viewModel.bindFlag
              ? FutureBuilder<List<List<String>>>(
                future: viewModel.getTimeTable(),
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  } else if (snapshot.hasError) {
                    return Center(child: Text('加载失败: ${snapshot.error}'));
                  } else {
                    var courseTableData = snapshot.data!;
                    if (courseTableData.isEmpty) {
                      courseTableData = List.generate(
                        6,
                        (_) => List.generate(7, (_) => ''),
                      );
                    }
                    return snapshot.data == null
                        ? Text("err")
                        : CourseTableWidget(courseTableData);
                  }
                },
              )
              : BindAccountWidget(),
          if (viewModel.cookie.isNotEmpty) ScoreDialogWidget(),
        ],
      ),
    );
  }
}
