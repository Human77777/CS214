/*
 * Mingen Liu
 * Haajrah Salman
 */
package view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import application.Song;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class SongController {

    @FXML
    private TextField name;
    @FXML
    private TextField artist;
    @FXML
    private TextField album;
    @FXML
    private TextField year;
    @FXML
    private ListView<Song> listofSong;
    @FXML

    private ObservableList<Song> obsList = FXCollections.observableArrayList();
    
    public void start(Stage primaryStage) {
        File data = new File("src/data/songs.txt");
        obsList = FXCollections.observableArrayList();
        
        if (data.exists() && !data.isDirectory()) {
            try (Scanner fileIn = new Scanner(data)) {
                int lines = 0;
                
                while (fileIn.hasNextLine()) {
                    lines++;
                    fileIn.nextLine();
                }
                
                fileIn.close();
                lines -= 2;
                fileIn.nextLine();
                fileIn.nextLine();
                
                if (lines % 4 == 0) {
                    for (int i = 0; i < lines; i += 4) {
                        obsList.add(new Song(fileIn.nextLine(), fileIn.nextLine(), fileIn.nextLine(), fileIn.nextLine()));
                    }
                    
                    FXCollections.sort(obsList);
                } else {
                    new Alert(AlertType.WARNING, "Formatting of file is not modulus 4").showAndWait();
                }
                
                fileIn.close();
            } catch (FileNotFoundException e) {
                new Alert(AlertType.WARNING, "File does not exist").showAndWait();
                e.printStackTrace();
            }
        }
        
        listofSong.setItems(obsList);
        
        if (!obsList.isEmpty()) {
            listofSong.getSelectionModel().selectFirst();
        }
        
        showSongDetails();
        
        listofSong.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> showSongDetails());
        
        primaryStage.setOnCloseRequest(event -> {
            try (PrintWriter write = new PrintWriter(new File("src/data/songs.txt"))) {
                write.println("Messing with song file format will result");
                write.println("IN LOSING ALL YOUR SONGS");
                
                for (int i = 0; i < obsList.size(); i++) {
                    write.println(obsList.get(i).getName());
                    write.println(obsList.get(i).getArtist());
                    write.println(obsList.get(i).getAlbum());
                    write.print(obsList.get(i).getYear());
                    
                    if (i != obsList.size() - 1) {
                        write.println("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        name.setText("");
        artist.setText("");
        album.setText("");
        year.setText("");
    }



    @FXML
    public void btnAdd() {
        String songName = name.getText();
        String songArtist = artist.getText();
        String songAlbum = album.getText();
        String songYear = year.getText();
     // Check if any of the fields are empty
        if (songName.isEmpty() || songArtist.isEmpty()) {
            showErrorAlert("Error", "Please fill out the song name or artist fields");
            return;
        }

        for (Song song : obsList) {
            if (song.getName().equals(songName) && song.getArtist().equals(songArtist)) {
                showErrorAlert("Error", "Song already exists in library");
                return;
            }
        }

        Song newSong = new Song(songName, songArtist, songAlbum, songYear);

        
        if (obsList.contains(newSong)) {
            showErrorAlert("Error", "Song already exists in library");
            return;
        }

        // confirm adding song
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Add Song");
        alert.setContentText("Are you sure you want to add this song?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            // Add the new song to the list and sort it
            obsList.add(newSong);
            FXCollections.sort(obsList);

            
            listofSong.getSelectionModel().select(newSong);

            
            name.setText("");
            artist.setText("");
            album.setText("");
            year.setText("");
        } else {
            alert.close();
        }}

        
    @FXML
    public void btnEdit() {
    	
        Song selectedSong = listofSong.getSelectionModel().getSelectedItem();

        
        if (selectedSong == null) {
            showErrorAlert("Error", "Please select a song to edit");
            return;
        }

        
        String songName = name.getText();
        String songArtist = artist.getText();
        String songAlbum = album.getText();
        String songYear = year.getText();

        
        if (songName.isEmpty() || songArtist.isEmpty()) {
            showErrorAlert("Error", "Please fill out the song name or artist fields");
            return;
        }

        
        for (Song song : obsList) {
            if (song.getName().equals(songName) && song.getArtist().equals(songArtist) && !song.equals(selectedSong)) {
                showErrorAlert("Error", "Song already exists in library");
                return;
            }
        }

        // Confirm the action before editing song
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Edit Song");
        alert.setContentText("Are you sure you want to edit this song?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            // Update the song's properties
            selectedSong.setName(songName);
            selectedSong.setArtist(songArtist);
            selectedSong.setAlbum(songAlbum);
            selectedSong.setYear(songYear);

            // Sort the list and select the edited song
            FXCollections.sort(obsList);
            listofSong.getSelectionModel().select(selectedSong);

            // Clear the input fields
            name.setText("");
            artist.setText("");
            album.setText("");
            year.setText("");
        } else {
            alert.close();
        }
    }


    @FXML
    public void btnDelete() {
        Song selectedSong = listofSong.getSelectionModel().getSelectedItem();

        
        if (selectedSong == null) {
            showErrorAlert("Error", "Please select a song to delete");
            return;
        }

        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Song: " + selectedSong.getName());
        alert.setContentText("Are you sure you want to delete this song?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            
            obsList.remove(selectedSong);
            listofSong.getSelectionModel().clearSelection();

            name.setText("");
            artist.setText("");
            album.setText("");
            year.setText("");
        }
    }

    private void showSongDetails() {
		if(listofSong.getSelectionModel().getSelectedIndex() < 0) {
			return;
		}
		
		Song song = listofSong.getSelectionModel().getSelectedItem();
		name.setText(song.getName());
		artist.setText(song.getArtist());
		album.setText(song.getAlbum());
		year.setText(song.getYear());
	}

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
   
}
