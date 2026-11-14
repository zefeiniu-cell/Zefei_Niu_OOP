# Zefei_Niu_OOP

**PROG2004 Assessment 1 - Arcana Health Centre**  
**Author:** Zefei Niu (`zniu16`)

Arcana Health Centre models one clinic day's appointments in Java. It has fourteen example staff across six types: four GPs and two of each other type. Patients can book, view, cancel and change appointment times.

The patient console follows the tutor's feedback on 13 November about making choices while the program runs. A separate demonstration covers Parts 3 and 6. Both modes use the same booking rules.

## Running the program

Open the project folder in VS Code with Java support and a JDK configured. Open `src/AssignmentOne.java` and run `main`.

| Launch choice | What it does |
| --- | --- |
| `1` | Opens the patient booking desk for the weekday entered |
| `2` | Runs the staff and appointment demonstration, including refused operations |
| `0` | Exits |

Only the Java standard library is needed. With JDK commands available, compile and run from the project root:

```text
javac -Xlint:all -d bin -sourcepath src src/AssignmentOne.java
java -cp bin AssignmentOne
```

To run the scripted demonstration directly:

```text
java -cp bin AssignmentOne --demo
```

`expected-output.txt` matches the direct `--demo` run. Choosing `2` adds launch-menu lines before the same demonstration.

## Using the patient console

Enter today's weekday, then a mobile number and patient name. For example, use `Wednesday`, `0412345678` and `Romy Lovell`.

| Patient choice | Action |
| --- | --- |
| `1 Book` | Shows on-duty professionals, then the selected person's details and free times |
| `2 My appointments` | Shows this patient's appointments in time order |
| `3 Doctors` | Lists the day's doctors and other health professionals |
| `4 Cancel` | Lets the patient select a numbered appointment and confirm cancellation |
| `5 Change time` | Lets the patient select a booking and request another time with the same professional |
| `0 Change patient` | Returns to the mobile-number prompt, keeping the same day's records |

To book, choose `1`, enter staff ID `100101`, time `09:00`, then confirm with `y`. The first successful booking creates the patient record; later bookings reuse it.

For cancellation or a time change, select the number beside your appointment. This number is its list position. Confirmation `n` leaves the record unchanged. Input `0` goes back; at the mobile prompt it ends the session. Records clear when the program closes.

## Model rules and assumptions

- The operator supplies today's weekday. The program models that day without reading the system date or clock. The scripted examples use Wednesday and a separate Saturday clinic.
- The standard schedule is 08:00-16:00 in thirty-minute slots. I treat 16:00 as closing time, giving a last start of 15:30. `ClinicSchedule` also supports other hours, intervals and lunch breaks.
- Staff must be registered and work that day. Their roster can change before registration, then stays fixed for that object. A different roster needs a new staff object, keeping advertised availability consistent with bookings.
- Staff IDs are six-digit strings. This is a model choice and keeps leading zeroes. Equality between professionals is based on ID, so another object with the same ID is matched to the registered professional.
- Mobiles have ten digits beginning with `04`; spaces and hyphens are removed. Each mobile belongs to one patient. A different name cannot reuse it. Each clinic allocates internal patient numbers from `P0001` to `P9999`.
- Within a clinic, neither the professional nor the patient can have two appointments at the same time. A failed booking or move leaves the existing records unchanged. A cancelled or moved appointment releases its old slot.
- The patient console selects a local patient record; it has no account authentication. Clinical service and prescription methods produce demonstration text.

## Source files

The project has nineteen Java source files in `src`.

| File | Responsibility |
| --- | --- |
| [AssignmentOne.java](src/AssignmentOne.java) | Entry point, staff examples and demonstration |
| [PatientConsole.java](src/PatientConsole.java) | Patient input, results and confirmation |
| [Clinic.java](src/Clinic.java) | Staff, patients, appointments and booking rules |
| [HealthProfessional.java](src/HealthProfessional.java) | Abstract parent: ID, name, working days and shared behaviour |
| [Nurse.java](src/Nurse.java) | Abstract nursing branch with a clinical area and shared blood-sample behaviour |
| [Prescriber.java](src/Prescriber.java) | Capability contract for prescribing scope and prescription text |
| [GeneralPractitioner.java](src/GeneralPractitioner.java) | Billing and referrals; implements `Prescriber` |
| [Specialist.java](src/Specialist.java) | Specialty, referral rule and tests; implements `Prescriber` |
| [RegisteredNurse.java](src/RegisteredNurse.java) | Nurse with an authorised-immuniser flag |
| [NursePractitioner.java](src/NursePractitioner.java) | Nurse with prescribing scope; implements `Prescriber` |
| [Dietitian.java](src/Dietitian.java) | Nutrition focus and meal-plan behaviour |
| [Physiotherapist.java](src/Physiotherapist.java) | Treatment focus and exercise-program behaviour |
| [Patient.java](src/Patient.java) | Patient number, name, normalised mobile and equality |
| [Appointment.java](src/Appointment.java) | Links a patient, professional and time; implements `Comparable` |
| [TimeSlot.java](src/TimeSlot.java) | Valid clock time stored as minutes; comparison and display |
| [ClinicSchedule.java](src/ClinicSchedule.java) | Generates and validates the day's bookable slots |
| [Week.java](src/Week.java) | Fixed weekday names and conversion between full and short names |
| [Validator.java](src/Validator.java) | Shared checks and text normalisation |
| [Display.java](src/Display.java) | Shared table formatting and small display helpers |

## Four pillars in this design

| Pillar | Where to look | Why it is useful here |
| --- | --- | --- |
| Encapsulation | `HealthProfessional.getWorkingDays()` and `updateWorkingDays()`; private fields and `Clinic` list-copy getters | Outside code receives copies and uses checked operations to change data. Registered working days stay fixed. |
| Inheritance | `RegisteredNurse extends Nurse extends HealthProfessional`; `super(...)` and `super.toString()` | Common fields and behaviour are reused down the hierarchy; subclasses add their own details. |
| Polymorphism | `ArrayList<HealthProfessional>`; `PatientConsole.book()`; `Clinic.bookAppointment()` calling `getPreparationAdvice()` | A common type accepts all six professional types, while each object supplies its own services and output. |
| Abstraction | `HealthProfessional` abstract methods, abstract `Nurse`, and `Prescriber` | Shared contracts define what callers can ask. Prescribing is a capability spanning different branches. |

`Appointment` has a patient, professional and time slot; `Clinic` has their collections. These has-a relationships assemble collaborating objects.

## Main design choices

I keep booking rules in `Clinic` because clash checks need the whole appointment list. `PatientConsole` collects choices and calls those methods.

`TimeSlot` turns both `9:30` and `09:30` into 570 minutes, simplifying comparison and interval calculations. `Appointment.compareTo()` delegates to it. `Clinic.sortByTime()` sorts a copy, preserving booking order.

The six concrete professional types have private constructors and validating `create(...)` methods. Bad input returns `null`; `check(...)` supplies the reason. `Appointment.create(...)` rejects missing references. Future subclasses must follow the validation contract: `abstract` alone does not validate constructor arguments.

Patient cancellation and time changes check mobile, staff ID and current time before calling the existing operation. General cancellation methods remain available. Mobile-only cancellation refuses to guess between several bookings. These parameter variations are overloading; subclass implementations of shared behaviour demonstrate overriding.

## Checking the behaviour

The demonstration covers mixed types, identity by ID, validation, defensive copies, booking conflicts, filtering, sorting, both general cancellation methods, rebooking, time changes and a Saturday lunch schedule.

For a console check, book, move, cancel and rebook the freed time. Try an occupied slot or off-duty professional. After refusal, use `2 My appointments` to check that the original booking remains.

## Naming

### The idea

Arcana Health Centre takes its name from the phrase *Major Arcana*. I used one card theme for the fictional cast, so the clinic name, staff names and patient examples belong together.

The naming follows three rules:

1. Each staff member has a card association linked to their profession or area of care.
2. Each patient or validation example has an association linked to its part in the demonstration.
3. Some names echo a card's name; others suggest its imagery or theme. These are creative links chosen for this project.

The mapping below uses VIII for Strength and XI for Justice. Staff IDs and patient mobiles still determine identity in the program; the cards describe the fictional cast.

### Staff

| Professional | Role in the model | Card | Naming link |
| --- | --- | --- | --- |
| Dr Elias Magee | General practitioner | I - The Magician | Magee echoes the Magician; the broad skill theme fits a GP. |
| Dr Solana Reyes | General practitioner | XIX - The Sun | The name suggests sunlight, matching the health and energy theme. |
| Dr Rex Calloway | General practitioner | IV - The Emperor | A ruler-like name connects with order and leadership. |
| Dr Orla Mundy | General practitioner | XXI - The World | Completeness connects the World with a GP's whole-person view. |
| Dr Leona Hart | Cardiology specialist | VIII - Strength | Lion and heart wordplay links the name with strength and cardiology. |
| Dr Selene Marsh | Dermatology specialist | XVIII - The Moon | Moon imagery and looking beneath the surface link the name and specialty. |
| Stella Rivers | Registered nurse, immunisation | XVII - The Star | Star and flowing-water imagery connect the name with care and hope. |
| Jude Ashworth | Registered nurse, wound care | XX - Judgement | Renewal and recovery fit the wound-care example. |
| Priya Sterling | Nurse practitioner, chronic disease | II - The High Priestess | The knowledge theme fits the advanced nursing role. |
| Imogen Empey | Nurse practitioner, women's health | III - The Empress | Nurturing and care connect the card with the chosen clinical area. |
| Tempe Lawson | Dietitian, diabetes nutrition | XIV - Temperance | Tempe echoes Temperance; balance and moderation fit nutrition. |
| Rowan Wheeler | Dietitian, sports nutrition | X - Wheel of Fortune | Wheeler echoes the Wheel, with cycles linking to the sports-training theme. |
| Charlie Rhodes | Physiotherapist, sports injuries | VII - The Chariot | Movement and direction fit sports rehabilitation. |
| Ingrid Hermansen | Physiotherapist, back and neck pain | IX - The Hermit | Hermansen hints at the Hermit, with a quiet, gradual-recovery theme. |

Rex's leadership association is part of the naming idea. His implemented role is GP; the model does not assign him a separate clinic-manager role.

### Patients and validation examples

| Character | Card | Part in the current demonstration | Naming link |
| --- | --- | --- | --- |
| Finn Foley / `Finn F0ley` | 0 - The Fool | Invalid or unregistered details are refused. | Foley echoes the Fool and marks the deliberate mistake examples. |
| Vincent Pope | V - The Hierophant | A registered patient requests a repeat prescription. | Pope and the tradition theme fit a returning patient's routine visit. |
| Romy and Theo Lovell | VI - The Lovers | Two separate patient records; reusing Romy's mobile for Theo is refused. | Lovell links the pair to the relationship theme. |
| Justine Fairley | XI - Justice | Books a slot freed by another patient's cancellation. | Justine and Fairley use justice and fairness wordplay. |
| Hugo Mann | XII - The Hanged Man | His 10:00 appointment is moved to 14:00. | Mann echoes Man; pausing and reconsidering fit a time change. |
| Devlin Sweet | XV - The Devil | Appears in the nutrition and meal-plan examples. | Devlin and Sweet use temptation-and-food wordplay. |
| Tara Towers | XVI - The Tower | Attempts a time already booked with another patient. | Towers echoes the Tower, linking disruption with a booking conflict. |

Romy uses `0412345678`; Theo uses `0488123456`. Their linked names keep the relationship theme while each has a separate mobile. The ambiguous mobile-only cancellation example comes from Romy's own two bookings. Finn is used for refusal examples and is not a successfully registered demonstration patient.

### The card left out

The tables use twenty-one card associations. XIII - Death is deliberately left unassigned because its name would set the wrong tone for this fictional health centre.
