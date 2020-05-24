/*
package be.kdg.scoutsappadmin.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*

class testDataFactory {
    @RequiresApi(Build.VERSION_CODES.O)
    fun createPeriodeList(): List<Periode>? {
        val persoon1c1 = Consumptie("key","Lemming", System.currentTimeMillis())
        val persoon1c2 = Consumptie("key","Spotlijster", System.currentTimeMillis())
        val persoon1c3 = Consumptie("key","Agame", System.currentTimeMillis())
        val persoon1c4 = Consumptie("key","Agame", System.currentTimeMillis())
        val persoonc1 = Consumptie("key","Lemming", System.currentTimeMillis())
        val persoonc2 = Consumptie("key","Wasbeer", System.currentTimeMillis())
        val persoonc3 = Consumptie("key","Agame", System.currentTimeMillis())
        val persoon3c1 = Consumptie("key","Wasbeer", System.currentTimeMillis())
        val persoon3c2 = Consumptie("key","Lemming", System.currentTimeMillis())
        val persoon4c1 = Consumptie("key","Spotlijster", System.currentTimeMillis())
        val persoon1consumaties: MutableList<Consumptie> =
            ArrayList<Consumptie>()
        persoon1consumaties.add(persoon1c1)
        persoon1consumaties.add(persoon1c2)
        persoon1consumaties.add(persoon1c3)
        persoon1consumaties.add(persoon1c4)
        val persoonconsumaties: MutableList<Consumptie> =
            ArrayList<Consumptie>()
        persoonconsumaties.add(persoonc1)
        persoonconsumaties.add(persoonc2)
        persoonconsumaties.add(persoonc3)
        val persoon3consumaties: MutableList<Consumptie> =
            ArrayList<Consumptie>()
        persoon3consumaties.add(persoon3c1)
        persoon3consumaties.add(persoon3c2)
        val persoon4consumaties: MutableList<Consumptie> =
            ArrayList<Consumptie>()
        persoon4consumaties.add(persoon4c1)
        val persoon1 =
            Persoon("key","Lemming", "lemmingpasswoord", persoon1consumaties, Rol.LEIDER)
        val persoon2 =
            Persoon("key","Wasbeer", "wasbeerpasswoord", persoonconsumaties, Rol.GROEPSLEIDING)
        val persoon3 =
            Persoon("key","Spotlijster", "spotlijsterpasswoord", persoon3consumaties, Rol.LEIDER)
        val persoon4 =
            Persoon("key","Agame", "agamepasswoord", persoon4consumaties, Rol.LEIDER)

        val personen1dag1: MutableList<Persoon> =
            ArrayList<Persoon>()
        personen1dag1.add(persoon1)
        personen1dag1.add(persoon2)
        personen1dag1.add(persoon3)
        personen1dag1.add(persoon4)
        val personen1dag2: List<Persoon> = ArrayList<Persoon>()
        personen1dag1.add(persoon2)
        personen1dag1.add(persoon4)
        val personen1dag3: List<Persoon> = ArrayList<Persoon>()
        personen1dag1.add(persoon1)
        personen1dag1.add(persoon3)
        val p1d1datum = LocalDate.of(2020, 7, 16).toString()
        val p1d2datum = LocalDate.of(2020, 7, 17).toString()
        val p1d3datum = LocalDate.of(2020, 7, 18).toString()
        val p1d4datum = LocalDate.of(2020, 7, 19).toString()
        val p1d5datum = LocalDate.of(2020, 7, 20).toString()
        val p2d1datum = LocalDate.of(2021, 7, 16).toString()
        val p2d2datum = LocalDate.of(2021, 7, 16).toString()
        val p1d1 = Dag("key",p1d1datum, personen1dag1)
        val p1d2 = Dag("key",p1d2datum, personen1dag2)
        val p1d3 = Dag("key",p1d3datum, personen1dag3)
        val p1d4 = Dag("key",p1d4datum, personen1dag1)
        val p1d5 = Dag("key",p1d5datum, personen1dag3)
        val p2d1 = Dag("key",p2d1datum, personen1dag3)
        val p2d2 = Dag("key",p2d2datum, personen1dag1)
        val p1Dagen: MutableList<Dag> = ArrayList<Dag>()
        p1Dagen.add(p1d1)
        p1Dagen.add(p1d2)
        p1Dagen.add(p1d3)
        p1Dagen.add(p1d4)
        p1Dagen.add(p1d5)
        val p2Dagen: List<Dag> = ArrayList<Dag>()
        p1Dagen.add(p2d1)
        p1Dagen.add(p2d2)
        val p1 = Periode("key","Kamp", p1Dagen,personen1dag1 )
        val p2 = Periode("key","ScoutsJaar",p2Dagen,personen1dag2)
        val periodeList: MutableList<Periode> = ArrayList<Periode>()

        periodeList.add(p1)
        periodeList.add(p2)

        return periodeList
    }

}*/
