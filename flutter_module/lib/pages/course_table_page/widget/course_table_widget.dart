import 'package:flutter/material.dart';
import 'package:flutter_module/pages/course_table_page/view_models/course_table_view_model.dart';
import 'package:flutter_module/them/app_colors.dart';
import 'package:provider/provider.dart';

class CourseTableWidget extends StatelessWidget {
  final List<List<String>> tableData;

  const CourseTableWidget(this.tableData, {super.key});

  @override
  Widget build(BuildContext context) {
    final viewModel = context.read<CourseTableViewModel>();

    final weekdays = ['一', '二', '三', '四', '五'];
    if (!viewModel.isColumnEmpty(tableData, 5)) {
      weekdays.add("六");
    }
    if (!viewModel.isColumnEmpty(tableData, 6)) {
      weekdays.add("日");
    }
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 0, horizontal: 10),
      child: Column(
        children: [
          // 顶部按钮
          CourseTableControlWidget(),
          // 表头
          CourseTableThWidget(weekdays: weekdays),
          // 表体
          CourseTableBodyWidget(
            colLength: weekdays.length,
            tableData: tableData,
          ),
        ],
      ),
    );
  }
}

class CourseTableControlWidget extends StatelessWidget {
  const CourseTableControlWidget({super.key});

  @override
  Widget build(BuildContext context) {
    final viewModel = context.watch<CourseTableViewModel>();

    void pickDate(
      BuildContext context,
      void Function(DateTime date) onPicked,
    ) async {
      final DateTime? picked = await showDatePicker(
        context: context,
        initialDate: DateTime.now(),
        firstDate: DateTime(2020),
        lastDate: DateTime(2030),
        locale: const Locale('zh'),
      );
      if (picked != null) {
        onPicked(picked);
      }
    }

    return Padding(
      padding: const EdgeInsets.fromLTRB(0, 46, 0, 0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          //上一周
          TextButton(
            onPressed: () {
              viewModel.decreaseWeekNumber();
            },
            child: const Text('上一周'),
          ),

          //日期选择
          Column(
            children: [
              Container(
                padding: const EdgeInsets.fromLTRB(5, 0, 0, 0),
                child: DropdownButton<String>(
                  value: viewModel.selectTerm,
                  borderRadius: BorderRadius.circular(16),
                  onChanged: (val) {
                    viewModel.setSelectTableTerm(val!);
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
              ),
              GestureDetector(
                onTap: () {
                  pickDate(context, (pickedData) {
                    viewModel.setWeekStartDate(pickedData.toString());
                  });
                },
                child: Text(
                  viewModel.weekNumber == 0
                      ? '请选择第一周的日期'
                      : '第${viewModel.weekNumber}周',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
              ),
            ],
          ),

          //下一周
          TextButton(
            onPressed: () {
              viewModel.increaseWeekNumber();
            },
            child: const Text('下一周'),
          ),
        ],
      ),
    );
  }
}

class CourseTableThWidget extends StatelessWidget {
  final List<String> weekdays;

  const CourseTableThWidget({super.key, required this.weekdays});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(0, 0, 5, 0),
          child: Text('0', style: TextStyle(color: Colors.transparent)),
        ),
        ...List.generate(weekdays.length, (index) {
          return Expanded(
            child: Center(
              child: Text(
                weekdays[index],
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
            ),
          );
        }),
      ],
    );
  }
}

class CourseTableBodyWidget extends StatelessWidget {
  final int colLength;
  final List<List<String>> tableData;

  const CourseTableBodyWidget({
    super.key,
    required this.colLength,
    required this.tableData,
  });

  @override
  Widget build(BuildContext context) {
    final appColors = Theme.of(context).extension<AppColors>()!;
    final viewModel = context.read<CourseTableViewModel>();

    return Expanded(
      child: SingleChildScrollView(
        child: Column(
          children: [
            //行
            ...List.generate(6, (rowIndex) {
              return IntrinsicHeight(
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Center(
                      child: Padding(
                        padding: const EdgeInsets.fromLTRB(0, 0, 5, 0),
                        child: Text(
                          '${rowIndex + 1}',
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                      ),
                    ),

                    //单元格
                    ...List.generate(colLength, (colIndex) {
                      var textData = tableData[rowIndex][colIndex];
                      return Expanded(
                        child: Container(
                          margin: const EdgeInsets.all(3),
                          padding: const EdgeInsets.all(3),
                          decoration: BoxDecoration(
                            color:
                                textData.isEmpty
                                    ? appColors.bgScreen
                                    : viewModel.getColorForCell(
                                      tableData[rowIndex][colIndex],
                                      viewModel.getUniqueNonEmpty(tableData),
                                    ),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            viewModel.textFormat(textData),
                            style: TextStyle(color: Colors.black),
                          ),
                        ),
                      );
                    }),
                  ],
                ),
              );
            }),
          ],
        ),
      ),
    );
  }
}
