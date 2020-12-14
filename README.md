# JMSMe
## Componenta echipei:
- Bizu Andreea-Felicia
- Tunaru Gabriel-Stefan
- Galuska Vlad-Cristian
- Nicolescu Alexandru-Leonard
## Setup
- Descarcati ActiveMQ http://activemq.apache.org/activemq-5145-release
- Rulati activemq.bat start (din apache-activemq-5.14.5\bin)
- Importare proiect
- Rulare Publisher(trebuie dat ca si argument un nume)
- Rulare Reader
## Arhitectura Sistemului
Aplicatia este formata dintr-un set de generatori si consumatori de evenimente.Am folosit JMS pentru partea de dispatcher, folosind resurse de tip Topic pentru a modela evenimentele ca mesaje asincrone.
## Descrierea generala
Sistem de stiri<br>
Aplicatia implementeaza un sistem de stiri orientat pe evenimente. Un
eveniment este aparitia, modificarea sau stergerea unei stiri, iar
stirile sunt organizate in domenii.
Stirile au si alte atribute cum ar fi: data primei publicari, data
ultimei modificari, sursa de informatie, autorul articolului etc.
Actorii din sistem sunt de doua tipuri: editori de stiri si cititori.
Editorii trebuie sa poata afla in timp real care este numarul de
cititori pentru stirile de interes. Pentru aceasta, ei se pot declara
interesati de aparitia unui eveniment gen "stire citita". Cititorii se
pot abona la una sau mai multe stiri, specificand domeniile de interes
si alte atribute (data, sursa etc.).
## Detalii despre implementare
Cititorul(`Reader`) se poate abona la o stire(`News`) prin specificarea domain-ului si source-ului(considerat unic). Un cititor se poate abona la mai multe stiri.
Editorul(`Publisher`) este cel care creeaza/editeaza/sterge stirile. De fiecare daca cand o stire este modificata sau stearsa, cititorul primeste un eveniment. Pentru ca un cititor sa fie considerat activ trebuie sa citeasca stirea.
Tipurile de evenimente existente sunt salvate intr-un enum in clasa `NewsEvent`. Pentru tratarea evenimentelor, fiecare cititor si editor folosesc un `MessageListener`, iar in functie de evenimentul, tratam corespunzator.
Stirile impreuna cu numarul de cititori sunt salvati intr-un `HashMap`, de care ne folosim pentru a afla numarul de cititori activi la un moment dat.
