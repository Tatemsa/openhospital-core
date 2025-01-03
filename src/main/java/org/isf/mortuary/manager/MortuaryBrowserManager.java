package org.isf.mortuary.manager;

import java.util.List;

import org.isf.mortuary.model.Mortuary;
import org.isf.mortuary.service.MortuaryIoOperations;
import org.isf.utils.exception.OHException;
import org.springframework.stereotype.Component;

@Component
public class MortuaryBrowserManager {
	private final MortuaryIoOperations mortuaryIoOperations;

	public MortuaryBrowserManager(MortuaryIoOperations mortuaryIoOperations) {
		this.mortuaryIoOperations = mortuaryIoOperations;
	}
	public Mortuary add(Mortuary Mortuary) throws OHException {
		return mortuaryIoOperations.add(Mortuary);
	}

	public List<Mortuary> getAll() throws OHException{
		System.out.println(mortuaryIoOperations.getAll().get(0));
		return mortuaryIoOperations.getAll();
	}

	public Mortuary update(Mortuary mortuary) throws OHException {
		return mortuaryIoOperations.update(mortuary);
	}

	public void delete(Mortuary mortuary) throws OHException {
		mortuaryIoOperations.delete(mortuary);
	}



	/*
	 * Returns all the deaths with the specified description.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param start_index
	 * @param page_size
	 * @return all the Deaths with the specified description.
	 */
	/*public ArrayList<Mortuary> getDeaths(int start_index, int page_size) {
		try {
			return ioOperations.getDeaths(start_index, page_size);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Mortuary> getDeaths(int motif, String pavillon, int pat, GregorianCalendar dateFrom,
		GregorianCalendar dateTo, boolean isEntree, boolean isSortie, int start_index, int page_size) {
		try {
			return ioOperations.getDeaths(motif, pavillon, pat, dateFrom, dateTo, isEntree, isSortie, start_index, page_size);
		} catch (OHException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public int getMortuaryTotalRows(int motif, String pavillon, int pat, GregorianCalendar dateFrom,
		GregorianCalendar dateTo, boolean isEntree, boolean isSortie) {
		try {
			return ioOperations.getMortuaryTotalRows(motif, pavillon, pat, dateFrom, dateTo, isEntree, isSortie);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	public void generateDeathCertificate(int deathID, String jasperFileName, boolean show, boolean c) {
		try {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			Locale locale = new Locale("en", "US");
			parameters.put(JRParameter.REPORT_LOCALE, locale);
			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("deathID", String.valueOf(deathID)); // real param
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());

			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");

			File jasperFile = new File(sbFilename.toString());

			Connection conn = DbSingleConn.getConnection();

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(deathID) + ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);

			if (show) {
				if (Param.bool("INTERNALVIEWER")) {
					JasperViewer.viewReport(jasperPrint, false);
				} else {
					try {
						Runtime rt = Runtime.getRuntime();
						rt.exec(Param.string("VIEWER") + " " + PDFfile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			//e.printStackTrace();
		}
	}

	public void generateEtatSejours(Death death, String jasperFileName, boolean show, boolean c) {
		try {

			GregorianCalendar dateSortie = death.getDateSortieProvisoire();

			if(dateSortie == null) {
				JOptionPane.showMessageDialog(
					null,
					MessageBundle.getMessage("angal.mortuaryedit.providedateleaving"),
					MessageBundle.getMessage("angal.hospital"),
					JOptionPane.PLAIN_MESSAGE);
				return;
			}

			GregorianCalendar dateEntree = death.getDateEntree();
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

			String date1 = fmt.format(dateEntree.getTime());
			//fmt.setCalendar(death.getDateEntree());

			String date2 = fmt.format(dateSortie.getTime());
			//fmt.setCalendar(dateSortie);

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			Locale locale = new Locale("en", "US");
			parameters.put(JRParameter.REPORT_LOCALE, locale);
			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("deathID", String.valueOf(death.getId())); // real param
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
			parameters.put("PAT_NAME", death.getPatientName());
			parameters.put("DECES_DATE", date1);
			parameters.put("DECES_DATE_SORTIE", date2);

			int nbjours = 0;
			float total = 0;

			ArrayList<StatePriceMortuaryStays> etats = getEtatPrix(death);
			if(etats == null) {
				return;
			}
			if(etats != null) {
				for(StatePriceMortuaryStays state: etats) {
					nbjours += state.getNombre_jours();
					total += state.getMontant();
				}
			}
			parameters.put("TOTAL_JOURS", nbjours);
			parameters.put("TOTAL_MONTANT", total);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(etats);
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");

			File jasperFile = new File(sbFilename.toString());

			//Connection conn = DbSingleConn.getConnection();

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(death.getId()) + ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);

			if (show) {
				if (Param.bool("INTERNALVIEWER")) {
					JasperViewer.viewReport(jasperPrint, false);
				} else {
					try {
						Runtime rt = Runtime.getRuntime();
						rt.exec(Param.string("VIEWER") + " " + PDFfile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			//e.printStackTrace();
		}

	}

	private ArrayList<StatePriceMortuaryStays> getEtatPrix(Mortuary death) {
		ArrayList<StatePriceMortuaryStays> etats = new ArrayList<StatePriceMortuaryStays>();
		ArrayList<PlagePriceMortuary> plages = new ArrayList<PlagePriceMortuary>();
		StatePriceMortuaryStays etatPrix = null;
		GregorianCalendar dateSortie = death.getProvisionalReleaseDate();

		GregorianCalendar dateEntree = death.getEnteredDate();
		try {
			int n = pio.dateDiff(dateSortie, dateEntree) + 1;
			plages = pio.getPrices();
			int i = 0;
			if(plages == null) {
				JOptionPane.showMessageDialog(
					null,
					MessageBundle.getMessage("angal.distype.nondefineprice"),
					MessageBundle.getMessage("angal.hospital"),
					JOptionPane.PLAIN_MESSAGE);
				return null;
			}
			final int N = n;
			while(n > 0 && i < plages.size()) {
				PlagePriceMortuary plage = plages.get(i);
				int k = i+1;
				int nb = (plage.getNbJourMax() - plage.getNbJourmin() + 1);
				int min0 = 1;
				if(min0 < plage.getNbJourmin() && i==0) {
					JOptionPane.showMessageDialog(
						null,
						MessageBundle.getMessage("angal.distype.nondefineprice") + " "+MessageBundle.getMessage("angal.distype.for")+ " "+min0+ " "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+(plage.getNbJourmin() -1)+" "+MessageBundle.getMessage("angal.distype.days"),
						MessageBundle.getMessage("angal.hospital"),
						JOptionPane.PLAIN_MESSAGE);
					return null;
				}

				if(k < plages.size()) {
					PlagePriceMortuary plage2 = plages.get(k);
					if(plage.getNbJourMax() < (plage2.getNbJourmin() -1) && (plage2.getNbJourmin() -1) == (plage.getNbJourMax() + 1)) {
						JOptionPane.showMessageDialog(
							null,
							MessageBundle.getMessage("angal.distype.nondefineprice") + " "+MessageBundle.getMessage("angal.distype.for")+ " "+(plage2.getNbJourmin() - 1)+" "+MessageBundle.getMessage("angal.distype.days"),
							MessageBundle.getMessage("angal.hospital"),
							JOptionPane.PLAIN_MESSAGE);
						return null;
					}
					else if(plage.getNbJourMax() < (plage2.getNbJourmin() -1) && (plage2.getNbJourmin() -1) > (plage.getNbJourMax() + 1)) {
						JOptionPane.showMessageDialog(
							null,
							MessageBundle.getMessage("angal.distype.nondefineprice") + " "+MessageBundle.getMessage("angal.distype.for")+ " "+(plage.getNbJourMax() +1)+ " "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+(plage2.getNbJourmin() - 1)+" "+MessageBundle.getMessage("angal.distype.days"),
							MessageBundle.getMessage("angal.hospital"),
							JOptionPane.PLAIN_MESSAGE);
						return null;
					}
				}
				if(n <= nb) {
					String desc = plage.getDescription()+" "+MessageBundle.getMessage("angal.mortuarybrowser.from")+" "+plage.getNbJourmin()+" "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+plage.getNbJourMax()+" "+MessageBundle.getMessage("angal.mortuarybrowser.days");
					etatPrix = new StatePriceMortuaryStays(desc, plage.getPrixJournalier(),  n, n* plage.getPrixJournalier());
					etats.add(etatPrix);
					n = 0;
					i++;
				}
				else {
					String desc = plage.getDescription()+" "+MessageBundle.getMessage("angal.mortuarybrowser.from")+" "+plage.getNbJourmin()+" "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+plage.getNbJourMax()+" "+MessageBundle.getMessage("angal.mortuarybrowser.days");
					etatPrix = new StatePriceMortuaryStays(desc, plage.getPrixJournalier(), nb, nb* plage.getPrixJournalier());
					etats.add(etatPrix);
					n = n - nb;
					int j = i+1;
					if(j == plages.size() && n>0) {
						JOptionPane.showMessageDialog(
							null,
							MessageBundle.getMessage("angal.distype.nondefinepricemore") + plage.getNbJourMax()+ " " +MessageBundle.getMessage("angal.distype.days"),
							MessageBundle.getMessage("angal.hospital"),
							JOptionPane.PLAIN_MESSAGE);
						return null;
					}
					i++;
				}
			}
		}catch(OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
		return etats;
	}

	public boolean patientIsDied(int code) {
		boolean result = false;
		/**
		 *
		 * method that update an existing Death in the db
		 * @param patient
		 * @return true - if the existing Death has been updated
		 *
		try {
			result = ioOperations.patientIsDied(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return result;
	}*/
}
