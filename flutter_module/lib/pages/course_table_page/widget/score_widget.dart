import 'package:flutter/material.dart';
import 'package:flutter_module/common/string_utils.dart';
import 'package:flutter_module/pages/course_table_page/view_models/course_table_view_model.dart';
import 'package:flutter_module/pages/course_table_page/view_models/score_view_model.dart';
import 'package:flutter_module/them/app_colors.dart';
import 'package:provider/provider.dart';

class ScoreDialogWidget extends StatelessWidget {
  const ScoreDialogWidget({super.key});

  @override
  Widget build(BuildContext context) {
    final appColors = Theme.of(context).extension<AppColors>()!;
    return Positioned(
      bottom: 16,
      right: 16,
      child: SizedBox(
        width: 46,
        height: 46,
        child: FloatingActionButton(
          shape: const CircleBorder(),
          backgroundColor: appColors.bgPrimary,
          onPressed: () {
            showDialog(
              context: context,
              builder: (_) {
                return ChangeNotifierProvider(
                  create:
                      (_) => ScoreViewModel(
                        cookie: context.read<CourseTableViewModel>().cookie,
                      ),
                  child: ScoreDialogBodyWidget(),
                );
              },
            );
          },
          child: Icon(
            Icons.search,
            color: appColors.primary,
          ),
        ),
      ),
    );
  }
}

class ScoreDialogBodyWidget extends StatelessWidget {
  const ScoreDialogBodyWidget({super.key});

  @override
  Widget build(BuildContext context) {
    final tableThText = ["课程名", "成绩", "学分", "属性"];
    final appColors = Theme.of(context).extension<AppColors>()!;
    var viewModel = context.watch<ScoreViewModel>();

    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Container(
        decoration: BoxDecoration(
          border: Border.all(
            color: appColors.greyMedium,
            width: 2
          ),
          borderRadius: BorderRadius.circular(16)
        ),
        padding: const EdgeInsets.all(10),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              '查询成绩',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Row(
              children: List.generate(tableThText.length, (thIndex) {
                return (thIndex == 0)
                    ? Expanded(
                      child: Center(
                        child: Text(
                          tableThText[thIndex],
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 16,
                          ),
                        ),
                      ),
                    )
                    : SizedBox(
                      width: 46,
                      child: Center(
                        child: Text(
                          tableThText[thIndex],
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 16,
                          ),
                        ),
                      ),
                    );
              }),
            ),
            Divider(thickness: 0.5, color: appColors.greyMedium),
            SingleChildScrollView(
              child: FutureBuilder(
                future: viewModel.getScoreTable(),
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  } else if (snapshot.hasError) {
                    return Center(child: Text(
                      '加载失败: 没有数据',
                      style: TextStyle(
                        color: appColors.err
                      ),
                    ));
                  } else {
                    return ScoreDialogBodyContentWidget(
                      tableData: snapshot.data!,
                    );
                  }
                },
              ),
            ),

            Divider(thickness: 0.5, color: appColors.greyMedium),

            SizedBox(height: 6),
            DropdownButton<String>(
              value: viewModel.selectScoreTerm,
              borderRadius: BorderRadius.circular(16),
              onChanged: (val) {
                viewModel.setScoreTerm(val!);
              },
              isDense: true,
              underline: SizedBox(),
              itemHeight: 52,
              menuWidth: 136,
              items:
                  viewModel.getTermList().map((val) {
                    return DropdownMenuItem(value: val, child: Text(val));
                  }).toList(),
            ),
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: Text('关闭'),
            ),
          ],
        ),
      ),
    );
  }
}

class ScoreDialogBodyContentWidget extends StatelessWidget {
  final List<List<String>> _tableData;

  const ScoreDialogBodyContentWidget({
    super.key,
    required List<List<String>> tableData,
  }) : _tableData = tableData;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: List.generate(_tableData.length, (index) {
        final rowData = _tableData[index];
        return SizedBox(
          height: 32,
          child: Row(
            children: List.generate(rowData.length, (index) {
              return (index == 0)
                  ? Expanded(
                    child: Text(
                      StringUtils.limitStrLength(rowData[index], 10),
                      style: TextStyle(fontSize: 14),
                    ),
                  )
                  : SizedBox(
                    width: 46,
                    child: Center(
                      child: Text(
                        rowData[index],
                        style: TextStyle(fontSize: 14),
                      ),
                    ),
                  );
            }),
          ),
        );
      }),
    );
  }
}
