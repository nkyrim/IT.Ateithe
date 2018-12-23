package com.nkyrim.itapp.domain.courseselection;

import java.util.ArrayList;

public final class CourseList extends ArrayList<ArrayList<Course>> {
	private static final long serialVersionUID = -8564887738583526196L;

	public CourseList() {
		for (int i = 0; i < 7; i++) {
			add(new ArrayList<Course>());
		}

		// hardcoded course table
		get(0).add(new Course("5101", "Εισαγωγή στη Πληροφορική", true));
		get(0).add(new Course("5102", "Αλγοριθμική και Προγραμματισμός", true));
		get(0).add(new Course("5103", "Ψηφιακά Συστήματα", false));
		get(0).add(new Course("5104", "Μαθηματική Ανάλυση και Γραμμική Άλγεβρα", false));
		get(0).add(new Course("5105", "Δεξιότητες Επικοινωνίας/Κοινωνικά Δίκτυα", true));
		get(1).add(new Course("5201", "Αντικειμενοστραφής Προγραμματισμός", true));
		get(1).add(new Course("5202", "Εισαγωγή στα Λειτουργικά Συστήματα", true));
		get(1).add(new Course("5203", "Διακριτά Μαθηματικά", false));
		get(1).add(new Course("5204", "Γλώσσες και Τεχνολογίες Ιστού", true));
		get(1).add(new Course("5205", "Πληροφοριακά Συστήματα Ι", false));
		get(2).add(new Course("5301", "Αριθμητική Ανάλυση & Προγραμματισμός Επιστημονικών Εφαρμογών", true));
		get(2).add(new Course("5302", "Δομές Δεδομένων και Ανάλυση Αλγορίθμων", true));
		get(2).add(new Course("5303", "Οργάνωση και Αρχιτεκτονική Υπολογιστικών Συστημάτων", true));
		get(2).add(new Course("5304", "Αλληλεπίδραση Ανθρώπου-Μηχανής & Ανάπτυξη Διεπιφανειών Χρήστη", true));
		get(2).add(new Course("5305", "Συστήματα Διαχείρισης Βάσεων Δεδομένων", true));
		get(3).add(new Course("5401", "Τεχνητή Νοημοσύνη:Γλώσσες και Τεχνικές", true));
		get(3).add(new Course("5402", "Τηλεπικοινωνίες και Δίκτυα Υπολογιστών", true));
		get(3).add(new Course("5403", "Μεθοδολογίες Προγραμματισμού", true));
		get(3).add(new Course("5404", "Τεχνολογία Βάσεων Δεδομένων", true));
		get(3).add(new Course("5405", "Θεωρία Πιθανοτήτων και Στατιστική", true));
		get(4).add(new Course("5501", "Αρχές Σχεδίασης Λειτουργικών Συστημάτων", false));
		get(4).add(new Course("5502", "Μηχανική Λογισμικού Ι", true));
		get(4).add(new Course("5503", "Δίκτυα Υπολογιστών", true));
		get(4).add(new Course("5504", "Ανάπτυξη Διαδικτυακών Συστημάτων & Εφαρμογών", true));
		get(4).add(new Course("5505", "Επιχειρησιακή Έρευνα", false));
		get(5).add(new Course("5601", "Πληροφοριακά Συστήματα ΙΙ", false));
		get(5).add(new Course("5602", "Μηχανική Λογισμικού ΙI", false));
		get(5).add(new Course("5603", "Οργάνωση Δεδομένων και Εξόρυξη Πληροφορίας", false));
		get(5).add(new Course("5604", "Μηχανική Μάθηση", false));
		get(5).add(new Course("5605", "Ευφυή Συστήματα", false));
		get(5).add(new Course("5606", "Ασφάλεια Πληροφοριακών Συστημάτων", false));
		get(5).add(new Course("5607", "Ειδικά Θέματα Δικτύων ΙI", true));
		get(5).add(new Course("5608", "Δίκτυα Ασύρματων και Κινητών Επικοινωνιών", false));
		get(5).add(new Course("5610", "Διοίκηση και Διαχείριση Έργων Πληροφορικής", false));
		get(5).add(new Course("5611", "Αναγνώριση Προτύπων - Νευρωνικά Δίκτυα", false));
		get(5).add(new Course("5612", "Δίκτυα Καθοριζόμενα από Λογισμικό", false));
		get(5).add(new Course("5613", "Διαδίκτυο των Πραγμάτων", false));
		get(6).add(new Course("5701", "Ανάπτυξη και Διαχείριση Ολοκληρωμένων Πλ. Συστημάτων & Εφαρμογών", true));
		get(6).add(new Course("5702", "Τεχνολογία Πολυμέσων", true));
		get(6).add(new Course("5703", "Γραφικά Υπολογιστών", false));
		get(6).add(new Course("5704", "Προηγμένες Αρχιτεκτονικές Υπολογιστών και Παράλληλα Συστήματα", false));
		get(6).add(new Course("5705", "Ειδικά Θέματα Δικτύων Ι", true));
		get(6).add(new Course("5706", "Διαδικτυακές Υπηρεσίες Προστιθέμενης Αξίας", false));
		get(6).add(new Course("5710", "Σημασιολογικός Ιστός", false));
		get(6).add(new Course("5711", "Διαχείριση Συστήματος και Υπηρεσιών DBMS", false));
	}

}