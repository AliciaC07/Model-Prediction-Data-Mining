import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.core.Instance;

import java.io.*;
import java.util.Scanner;

public class Predict {

    public static void main(String[] args) throws Exception {
        //Se carga el dataset
        DataSource dataSource = new DataSource("src/files/flags.arff");
        Instances dataset = dataSource.getDataSet();
        dataset.setClass(dataset.attribute("religion"));
        int [] listatribute =new int[]{0,3,4,7,8,9,10,11,12,13,14,15,16,17,18,20,21,23,22,24,25,26,27,28,29};

        //Se eliminan los atributos que no vamos a utilizar para nuestro modelo

        Remove removeatribute = new Remove();
        removeatribute.setAttributeIndicesArray(listatribute);
        removeatribute.setInputFormat(dataset);
        Instances filteredData = Filter.useFilter(dataset, removeatribute);

        //Se utiliza naiveBayes como clasificador
        NaiveBayes naiveBayesClasi = new NaiveBayes();
        naiveBayesClasi.buildClassifier(filteredData);
        Evaluation eval = new Evaluation(filteredData);
        eval.evaluateModel(naiveBayesClasi, filteredData);
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        Scanner imput = new Scanner(System.in);
        System.out.println("Enter the zone value: 1= NE, 2=SE, 3=SW, 4=NW");
        int zone = imput.nextInt();
        System.out.println("Enter the landmass value: 1=N.America, 2=S.America," +
                " \n3=Europe, 4=Africa, 4=Asia, 6=Oceania");
        int landmass = imput.nextInt();
        System.out.println("Enter the language value: 1=English, 2=Spanish, 3=French,\n" +
                " 4=German, 5=Slavic, 6=Other Indo-European, " +
                "7=Chinese, 8=Arabic, 9=Japanese/Turkish/Finnish/Magyar, " +
                "\n10=Others");
        int language = imput.nextInt();
        System.out.println("Enter the crosses value: Number of (upright) crosses 0, 1 or 2");
        int crosses = imput.nextInt();
        System.out.println("Enter the sunstars value: Number of sun or star symbols 0 - 50");
        int sunstars = imput.nextInt();

        String newData = landmass+","+zone+","+language+","+crosses+","+sunstars;
        System.out.println(newData);

        //Cuando se tiene la nueva instancia se coloca en un archivo arff
        File file = new File("src/files/model-flag-test.arff");
        FileWriter fr = new FileWriter(file, true);
        fr.write(newData);
        fr.close();

        //Se vuelve a cargar ese dataset con la instancia nueva
        DataSource dataSource2 = new DataSource("src/files/model-flag-test.arff");
        Instances dataset2 = dataSource2.getDataSet();
        //Aquí lo agregamos al dataset principal para hacer la predicción y probar el modelo con esa instancia

        filteredData.add(dataset2.get(0));
        System.out.println(filteredData.toSummaryString());
        Evaluation evaluation = new Evaluation(filteredData);
        evaluation.evaluateModel(naiveBayesClasi, filteredData);
        System.out.println(evaluation.toSummaryString("\nResults\n======\n", false));
        int prediction = (int) naiveBayesClasi.classifyInstance(filteredData.lastInstance());
        System.out.println(prediction);
        RemoveLinedata(newData);
        System.out.println("\nRESULT\n");
        if (prediction == 0){
            System.out.println("Catholic, value = 0");
        }else if (prediction == 1){
            System.out.println("Other Christian, value = 1");
        }else if (prediction == 2){
            System.out.println("Muslim, value = 2");
        }else if (prediction == 3){
            System.out.println("Buddhist, value = 3");
        }else if (prediction == 4){
            System.out.println("Hindu, value = 4");
        }else if (prediction == 5){
            System.out.println("Ethnic, value = 5");
        }else if (prediction == 6){
            System.out.println("Marxist, value = 6");
        }else if (prediction == 7){
            System.out.println("Others, value = 7");
        }




    }

    public static void RemoveLinedata(String last) throws IOException {
        File file = new File("src/files/model-flag-test.arff");
        File tempfile = new File("src/files/model-flag-test.arff");

        BufferedReader input = new BufferedReader(new FileReader("src/files/model-flag-test.arff"));
        String line = null;
        PrintWriter pw = new PrintWriter(new FileWriter(tempfile));

        while ((line = input.readLine()) != null) {
            if (!line.trim().equals(last)) {
                pw.println(line);
                pw.flush();
            }
        }
        pw.close();
        input.close();

        if (!file.delete()) {
            //System.out.println("Could not delete file");

        }
        if (!tempfile.renameTo(file)) {
            //System.out.println("Could not rename file");
        }
        FileWriter fr = new FileWriter(tempfile, true);
        fr.write("@relation flags\n" +
                "\n" +
                "@attribute landmass {1,2,3,4,5,6}\n" +
                "@attribute zone {1,2,3,4}\n" +
                "@attribute language {1,2,3,4,5,6,7,8,9,10}\n" +
                "@attribute crosses {0,1,2}\n" +
                "@attribute sunstars {0,1,2,3,4,5,6,7,9,10,14,15,22,50}\n" +
                "\n" +
                "@data\n");
        fr.close();
    }

}
