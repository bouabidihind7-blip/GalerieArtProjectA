package entities;

import javax.persistence.*;

@Entity
@Table(name = "oeuvre")
public class Oeuvre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "titre", nullable = false, length = 100)
    private String titre;

    @Column(name = "artiste", nullable = false, length = 100)
    private String artiste;

    @Column(name = "genre", length = 50)
    private String genre;

    @Column(name = "prix")
    private double prix;

    @Column(name = "quantite")
    private int quantite;

    @Column(name = "disponible")
    private boolean disponible;

    // ── Constructeurs ────────────────────────────────────────────
    public Oeuvre() {}

    // ── Getters / Setters ────────────────────────────────────────
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getTitre()                { return titre; }
    public void setTitre(String titre)      { this.titre = titre; }

    public String getArtiste()              { return artiste; }
    public void setArtiste(String artiste)  { this.artiste = artiste; }

    public String getGenre()                { return genre; }
    public void setGenre(String genre)      { this.genre = genre; }

    public double getPrix()                 { return prix; }
    public void setPrix(double prix)        { this.prix = prix; }

    public int getQuantite()                { return quantite; }
    public void setQuantite(int quantite)   { this.quantite = quantite; }

    public boolean isDisponible()                   { return disponible; }
    public void setDisponible(boolean disponible)   { this.disponible = disponible; }
}
