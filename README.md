# Zefei_Niu_OOP

PROG2004 Assessment 1 - Arcana Health Centre

Zefei Niu (`zniu16`)

This project models appointments for one day at Arcana Health Centre. It has patient input and a scripted demonstration. Both use the same `Clinic` methods. The examples use fourteen staff: four GPs and two of each of the other five types.

## How to run it

Open the project folder in VS Code with Java support and a JDK set up. Open `src/AssignmentOne.java` and run `main`.

| Startup choice | Action |
| --- | --- |
| `1` | Start patient input for the weekday you enter |
| `2` | Run the Part 3 and Part 6 scripted examples |
| `0` | Exit |

The project uses the Java standard library. From the project folder:

```text
javac -Xlint:all -d bin -sourcepath src src/AssignmentOne.java
java -cp bin AssignmentOne
```

To run the scripted demonstration directly:

```text
java -cp bin AssignmentOne --demo
```

`expected-output.txt` matches the `--demo` run. Choice `2` runs the same examples after the startup choices are printed. The records are kept in memory and disappear when the program ends.

## Patient input

Choose `1`, then enter a weekday, mobile and name. For a test, use `Wednesday`, `0412345678` and `Romy Lovell`.

| Patient choice | Action |
| --- | --- |
| `1 Book` | Choose an on-duty professional, view their details and select a free start time |
| `2 My appointments` | Show your appointments from earliest to latest |
| `3 Doctors` | Show the doctors and other professionals working that day |
| `4 Cancel` | Select one of your numbered appointments and confirm cancellation |
| `5 Change time` | Select an appointment and choose another time with the same professional |
| `0 Change patient` | Return to the mobile prompt while keeping the day's records |

For a first booking, choose `1`, staff ID `100101`, time `09:00`, then `y` to confirm. The patient record is created when the booking succeeds. Booking again with matching details uses that record.

For cancellation or a time change, select the number beside an appointment in your list. Entering `n` at confirmation keeps it unchanged. Entering `0` goes back; at the mobile prompt it ends the patient session.

## Rules used in this model

- The patient session uses the weekday entered at startup. The scripted demonstration uses Wednesday, then a separate Saturday clinic. The model receives the weekday as data; it does not read the system date or clock.
- Normal hours are 08:00-16:00, with a booking every thirty minutes. I use 16:00 as closing time, so the last booking starts at 15:30. `ClinicSchedule` can also use different hours, intervals and a lunch break.
- A professional must be registered and work that day. I lock the roster after registration so an edit cannot hide an existing booking. A new staff object is needed for a different roster.
- Staff IDs are strings containing six digits. This keeps starting zeroes. Two professional objects with the same ID represent the same person, and the clinic uses the registered object's details.
- A mobile has ten digits starting with `04`. Spaces and hyphens are removed. One mobile belongs to one patient, so another name cannot register with it. Patient numbers are given out from `P0001` to `P9999` in each clinic.
- A professional or patient cannot be booked twice at the same time within the clinic. If a booking or move is refused, the old records stay. Cancelling or moving frees the old slot.
- Names and mobiles identify model records. There is no login or account authentication. Clinical service and prescription methods return example text.

## Source files

The project has nineteen Java source files in `src`.

| File | Responsibility |
| --- | --- |
| [AssignmentOne.java](src/AssignmentOne.java) | Entry point, staff examples and demonstration |
| [PatientConsole.java](src/PatientConsole.java) | Patient input, confirmations and displayed results |
| [Clinic.java](src/Clinic.java) | Staff, patients, appointments and booking rules |
| [HealthProfessional.java](src/HealthProfessional.java) | Abstract parent: ID, name, working days and shared behaviour |
| [Nurse.java](src/Nurse.java) | Abstract nursing class with a clinical area and shared blood-sample method |
| [Prescriber.java](src/Prescriber.java) | Interface for prescribing scope and prescription text |
| [GeneralPractitioner.java](src/GeneralPractitioner.java) | Bulk-billing flag and referral method; implements `Prescriber` |
| [Specialist.java](src/Specialist.java) | Specialty, referral flag and test-order method; implements `Prescriber` |
| [RegisteredNurse.java](src/RegisteredNurse.java) | Nurse with an authorised-immuniser flag |
| [NursePractitioner.java](src/NursePractitioner.java) | Nurse with prescribing scope; implements `Prescriber` |
| [Dietitian.java](src/Dietitian.java) | Nutrition focus and meal-plan method |
| [Physiotherapist.java](src/Physiotherapist.java) | Treatment focus and exercise-program method |
| [Patient.java](src/Patient.java) | Patient number, name, normalised mobile and equality |
| [Appointment.java](src/Appointment.java) | Links a patient, professional and time; implements `Comparable` |
| [TimeSlot.java](src/TimeSlot.java) | Valid clock time stored as minutes; comparison and display |
| [ClinicSchedule.java](src/ClinicSchedule.java) | Generates and validates the day's bookable slots |
| [Week.java](src/Week.java) | Fixed weekday names and conversion between full and short names |
| [Validator.java](src/Validator.java) | Shared checks and text normalisation |
| [Display.java](src/Display.java) | Shared table formatting and small display helpers |

## OOP in this project

| Pillar | Where to look | Why I used it |
| --- | --- | --- |
| Encapsulation | Private fields, `HealthProfessional.getWorkingDays()` and `updateWorkingDays()`, and the list getters in `Clinic` | The getters return copies. Changes go through methods that check the input, and a registered roster stays fixed. |
| Inheritance | `RegisteredNurse extends Nurse extends HealthProfessional`; `super(...)` and `super.toString()` | Both nurse types reuse the common nursing code. Each level sets up and prints its own fields. |
| Polymorphism | `ArrayList<HealthProfessional>`, the staff loop in `AssignmentOne`, and `getPreparationAdvice()` in `Clinic.bookAppointment()` | One list holds all six types. The same method call gives the details or advice for the actual object. |
| Abstraction | Abstract `HealthProfessional` and `Nurse`, plus the `Prescriber` interface | The parent defines the shared questions. `Prescriber` lets doctors and nurse practitioners share an ability while keeping their own parent classes. |

`Appointment` holds a patient, a professional and a time slot. `Clinic` keeps the staff, patients and appointment lists. These are the has-a relationships in the model.

## Why I put the rules here

A single appointment only knows its own details. To find a time clash, I need to compare it with the other bookings, so that check belongs in `Clinic`. The demonstration calls these methods with fixed examples. `PatientConsole` reads choices and calls the same clinic methods.

`TimeSlot` turns both `9:30` and `09:30` into 570 minutes. I can compare that with 600 for `10:00`, or add thirty minutes for the next slot. `Appointment.compareTo()` uses this comparison. `Clinic.sortByTime()` sorts a copy so the original booking order stays available.

The six concrete professional types have private constructors. Their `create(...)` methods check the details before making an object. Bad input returns `null`, and `check(...)` gives the reason. `Appointment.create(...)` also refuses missing references. A new subclass must follow the same checking rule: `abstract` alone does not make its input valid.

The cancellation and time-change overloads that take a mobile also check the staff ID and current appointment time. They then call the existing operation. The mobile-only cancellation method gives a warning if there is more than one booking to choose from. Methods with the same name and different parameters show overloading. A subclass giving its own version of an inherited instance method shows overriding.

## Checks

The demonstration uses the different professional types and checks ID equality, bad input, returned copies, booking clashes, filters and sorting. It also shows cancellation by staff ID and time or by mobile, rebooking a freed slot, time changes and a Saturday clinic with lunch.

The examples print the appointments again after changes. A failed move keeps the original time, and a cancelled slot can be booked by another patient.

## Naming

Arcana Health Centre comes from the phrase Major Arcana. I kept the clinic and character names within the same theme.

For staff, I linked a card to their profession or area of care. For patients and invalid-input examples, I linked it to what happens in the demonstration. Some names use wordplay; others use an image or idea from the card.

These are the naming links chosen for this project. I use VIII for Strength and XI for Justice. The program still identifies professionals by ID and patient records by mobile.

### Staff

| Professional | Role in the model | Card | Naming link |
| --- | --- | --- | --- |
| Dr Elias Magee | General practitioner | I - The Magician | Magee echoes Magician. The broad-skills idea fits a GP. |
| Dr Solana Reyes | General practitioner | XIX - The Sun | Solana was chosen to suggest sunlight, health and energy. |
| Dr Rex Calloway | General practitioner | IV - The Emperor | Rex suggests a ruler and the Emperor's order theme. |
| Dr Orla Mundy | General practitioner | XXI - The World | The World suggests completeness, which I linked to a GP's whole-person care. |
| Dr Leona Hart | Cardiology specialist | VIII - Strength | Leona suggests a lion and Hart sounds like heart, linking the name to cardiology. |
| Dr Selene Marsh | Dermatology specialist | XVIII - The Moon | I linked Selene with the Moon, and looking under the surface with skin care. |
| Stella Rivers | Registered nurse, immunisation | XVII - The Star | Stars and water connect the name with care and hope. |
| Jude Ashworth | Registered nurse, wound care | XX - Judgement | Recovery and starting again fit the wound-care example. |
| Priya Sterling | Nurse practitioner, chronic disease | II - The High Priestess | Knowledge is the link to advanced nursing. |
| Imogen Empey | Nurse practitioner, women's health | III - The Empress | I used the nurture theme for women's health. |
| Tempe Lawson | Dietitian, diabetes nutrition | XIV - Temperance | Tempe echoes Temperance. Balance and moderation connect it with nutrition. |
| Rowan Wheeler | Dietitian, sports nutrition | X - Wheel of Fortune | Wheeler points to the Wheel; training cycles give the sports-nutrition link. |
| Charlie Rhodes | Physiotherapist, sports injuries | VII - The Chariot | Movement is the link to sports rehabilitation. |
| Ingrid Hermansen | Physiotherapist, back and neck pain | IX - The Hermit | Hermansen hints at the Hermit. Its quiet mood gives the recovery theme. |

Rex's role in the code is GP. The Emperor connection is used for the name.

### Patients and validation examples

| Character | Card | Part in the current demonstration | Naming link |
| --- | --- | --- | --- |
| Finn Foley / `Finn F0ley` | 0 - The Fool | Invalid or unregistered details are refused. | Foley echoes Fool and marks the deliberate input mistakes. |
| Vincent Pope | V - The Hierophant | A registered patient asks for a repeat prescription. | Pope and tradition fit the returning-patient example. |
| Romy and Theo Lovell | VI - The Lovers | Separate records; Theo's attempt to use Romy's mobile is refused. | Lovell links the two names with the relationship theme. |
| Justine Fairley | XI - Justice | Books a slot after another patient cancels it. | Justine suggests justice, and Fairley suggests fairness. |
| Hugo Mann | XII - The Hanged Man | The 10:00 booking moves to 14:00. | Mann echoes Man; reconsidering a plan connects it to a time change. |
| Devlin Sweet | XV - The Devil | Appears in the nutrition and meal-plan examples. | Devlin and Sweet give the food-and-temptation wordplay. |
| Tara Towers | XVI - The Tower | Tries a time already booked for another patient. | Towers points to the Tower; the booking clash is the disruption. |

Romy uses `0412345678` and Theo uses `0488123456`. They keep separate records. The mobile-only cancellation warning comes from Romy's two bookings, not a shared family number. Finn's examples are refused, so they do not add a registered demonstration patient.

I left XIII - Death out because its name felt out of place in a health centre. The tables use the other twenty-one card links.
