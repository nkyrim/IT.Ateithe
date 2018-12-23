package com.nkyrim.itapp.ui.grades;

import android.content.Context;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.student.Grade;
import com.nkyrim.itapp.domain.student.GradesInfo;
import com.nkyrim.itapp.domain.student.GradesSemester;
import com.nkyrim.itapp.domain.student.GradesTotal;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.ui.util.base.BaseLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

class GradeLoader extends BaseLoader<TaskResult<GradesTotal>> {

	public GradeLoader(Context context) {
		super(context);
	}

	@Override
	public TaskResult<GradesTotal> loadInBackground() {
		TaskResult<String> r = ItNet.retrievePersonalInfo();
		String body = r.getResult();

		GradesTotal totalGradeList;
		if(r.isSuccessful()) {
			Document doc = Jsoup.parse(body);

			Elements info = doc.select("div table tr[height=20] td");
			Elements grades = doc.select("#main > div > table > tbody > tr > td > span > table > tbody > tr ");

			// some unknown error
			if(info.isEmpty()) {
				return new TaskResult<>(false);
			}

			// Get general info
			String name, lname, am;
			lname = info.get(1).text();
			name = info.get(3).text();
			am = info.get(5).text();

			// Get latest grades
			String course, exam, grade;
			ArrayList<Grade> lastGrades = new ArrayList<>();
			Elements cells;
			// skip first row, contains titles
			if(grades.size() >= 2) {
				for (int i = 1; i < grades.size(); i++) {
					cells = grades.get(i).select("td");
					course = cells.get(1).text();
					exam = cells.get(2).text();
					grade = cells.get(3).text();

					lastGrades.add(new Grade(course, grade, exam));
				}
			}

			totalGradeList = new GradesTotal(new GradesInfo(lname + " " + name, am, lastGrades));
		} else {
			totalGradeList = null;
		}

		if(totalGradeList == null) return new TaskResult<>(false, r.getMessage());

		r = ItNet.retrieveGrades();
		body = r.getResult();
		if(r.isSuccessful()) {
			Document doc = Jsoup.parse(body);
			// check for error
			Elements noGrages = doc.select("#main>div>table>tbody>tr>td");
			if(noGrages != null && noGrages.size() == 2) {
				return new TaskResult<>(false, noGrages.get(1).text());
			}

			Elements grades = doc.select("td.groupHeader, [bgcolor=#fafafa], tr.subHeaderBack");

			GradesSemester gl = null;
			Grade g = null;
			String temp, code, course, avgGrade, theoryGrade, labGrade, exam, examTheory, examLab, noPassed, avgPassed;

			for (Element e : grades) {
				if(e.hasClass("groupHeader")) {
					gl = new GradesSemester(e.text());
					totalGradeList.add(gl);
				} else if(e.hasClass("grayfonts")) {
					temp = e.text();
					if(temp.contains("-Θ") || temp.contains("- Θ")) {
						theoryGrade = e.select("td.redFonts").text();
						examTheory = (e.select("span.tablecell") == null) ? "" : e.select("span.tablecell").text();
						if(g != null) {
							g.setGradeTheory(theoryGrade);
							g.setExamTheory(examTheory);
						}
					} else {
						labGrade = e.select("td.redFonts").text();
						examLab = (e.select("span.tablecell") == null) ? "" : e.select("span.tablecell").text();
						if(g != null) {
							g.setGradeLab(labGrade);
							g.setExamLab(examLab);
						}
					}
				} else if(e.hasClass("subHeaderBack")) {
					temp = e.text();
					noPassed = temp.substring(temp.indexOf(":") + 2, temp.indexOf("ΜΟ:"));
					avgPassed = temp.substring(temp.indexOf("ΜΟ:") + 4, temp.indexOf("ΔΜ:")).replace(".", ",");
					if(gl != null && gl.getNoPassed() == null) {
						gl.setNoPassed(noPassed);
						gl.setAvgPassed(avgPassed);
					}
				} else {
					temp = e.select("td.topBorderLight").first().ownText();
					code = temp.substring(0, temp.indexOf(" "));
					code = code.contains("-") ? "(" + code.substring(code.indexOf("-") + 1) : code;
					course = temp.substring(temp.indexOf(" ") + 1);
					avgGrade = e.select("span").get(1).text().replace(".", ",").trim();
					exam = (e.select("span.tablecell") == null) ? "" : e.select("span.tablecell").text();
					g = new Grade(code, course, avgGrade, exam);

					if(gl != null) gl.add(g);
				}
			}

			Element e = doc.select("tr.subHeaderBack").last();
			String avg = e.select("b span").first().text().replace("-", "").replace(".", ",").trim();
			String dm = e.select("b span").get(1).text().trim();
			String no = e.text().substring(e.text().indexOf(":") + 1, e.text().indexOf("ΜΟ:")).trim();

			totalGradeList.getInfo().setAvgGrade(avg);
			totalGradeList.getInfo().setPassedCourses(no);
			totalGradeList.getInfo().setTotalDM(dm);

			result = new TaskResult<>(totalGradeList);
		} else {
			result = new TaskResult<>(false, r.getMessage());
		}

		return result;
	}
}
