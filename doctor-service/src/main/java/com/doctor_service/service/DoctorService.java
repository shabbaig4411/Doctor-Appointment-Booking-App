package com.doctor_service.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.doctor_service.clients.AuthClient;
import com.doctor_service.config.S3Config;
import com.doctor_service.dto.*;
import com.doctor_service.entity.*;
import com.doctor_service.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorService {

    private final AuthClient authClient;
    private final DoctorRepository doctorRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final AppointmentDateRepository appointmentDateRepository;
    private final AmazonS3 s3Client;

    public DoctorService(AuthClient authClient,
                         DoctorRepository doctorRepository,
                         StateRepository stateRepository,
                         CityRepository cityRepository,
                         AreaRepository areaRepository,
                         AppointmentDateRepository appointmentDateRepository,
                         AmazonS3 s3Client) {
        this.authClient = authClient;
        this.doctorRepository = doctorRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
        this.appointmentDateRepository = appointmentDateRepository;
        this.s3Client = s3Client;
    }

    @Value("${app.s3.bucket}")
    private String s3BucketName;

    public Doctor saveDoctor(DoctorDto doctorDto, String doctorId) {
        // ---------- Handle State ----------
        State existingState = stateRepository
                .findByNameIgnoreCase(doctorDto.getState())
                .orElseGet(() -> {
                    State s = new State();
                    s.setName(doctorDto.getState());
                    return stateRepository.save(s);
                });
        // ---------- Handle City ----------
        City existingCity = cityRepository
                .findByNameIgnoreCase(doctorDto.getCity())
                .orElseGet(() -> {
                    City c = new City();
                    c.setName(doctorDto.getCity());
                    return cityRepository.save(c);
                });
        // ---------- Handle Area ----------
        Area existingArea = areaRepository
                .findByNameIgnoreCase(doctorDto.getArea())
                .orElseGet(() -> {
                    Area a = new Area();
                    a.setName(doctorDto.getArea());
                    return areaRepository.save(a);
                });

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId); // Extracted From Token
        doctor.setSpecialization(doctorDto.getSpecialization());
        doctor.setQualification(doctorDto.getQualification());
        doctor.setExperience(doctorDto.getExperience());
        doctor.setAddress(doctorDto.getAddress());
        doctor.setState(existingState);
        doctor.setCity(existingCity);
        doctor.setArea(existingArea);

        return doctorRepository.save(doctor);
    }


    public String saveDoctorImage(String doctorId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        // build a key (path) for S3, e.g. randomid+{ext}
        String key = UUID.randomUUID().toString() + ext;
        System.out.println(key); // 52353c9f-15a4-48ea-8268-ebbb1f6498e6.jpg

        try (InputStream inputStream = file.getInputStream()) {
            System.out.println(inputStream); // sun.nio.ch.ChannelInputStream@3f96e513

            ObjectMetadata metadata = new ObjectMetadata();
            long contentLength = file.getSize(); // important!
            if (contentLength > 0) {
                System.out.println(contentLength); // 22467

                metadata.setContentLength(contentLength);
            }
            String contentType = file.getContentType();
            System.out.println(contentType); // image/jpeg

            if (contentType != null) metadata.setContentType(contentType);

            PutObjectRequest putRequest = new PutObjectRequest(s3BucketName, key, inputStream, metadata);
                   // .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putRequest); // this will stream the upload using supplied metadata

            // return public url
            URL url = s3Client.getUrl(s3BucketName, key);
            System.out.println(url.toString()); // https://doctor-appointment-booking-app.s3.ap-south-1.amazonaws.com/eaf0ab6b-4b5d-4ebb-b02e-ded9f36805af.jpg
            Optional<Doctor> doctor = doctorRepository.findById(doctorId);
            doctor.get().setImageUrl(url.toString());
            doctorRepository.save(doctor.get());
            return url.toString();

        } catch (AmazonServiceException ase) {
            System.out.println("AmazonServiceException uploading file to S3" + ase);
            throw new RuntimeException("Failed Image Upload: S3 service error", ase);
        } catch (AmazonClientException ace) {
            System.out.println("AmazonClientException uploading file to S3" + ace);
            throw new RuntimeException("Failed Image Upload: S3 client error", ace);
        } catch (IOException ioe) {
            System.out.println("IOException reading multipart file" + ioe);
            throw new RuntimeException("Failed Image Upload", ioe);
        }

    }


    public AppointmentDate saveAppointmentDetails(AppointmentDetailsDto details, String doctorId) {

        Optional<AppointmentDate> existing =
                appointmentDateRepository.findByDoctorIdAndDate(doctorId, details.getDate());

        AppointmentDate appointment;

        if (existing.isPresent()) {
            appointment = existing.get();

            // 🟢 SAFE CLEAR (Do NOT replace set!!)
            Iterator<TimeSlots> it = appointment.getTimeSlots().iterator();
            while (it.hasNext()) {
                TimeSlots slot = it.next();
                it.remove();
                slot.setAppointmentDate(null); // required for orphanRemoval
            }

        } else {
            appointment = new AppointmentDate();
            appointment.setDateId(ULIDGenerator.generate());
            appointment.setDoctorId(doctorId);
            appointment.setDate(details.getDate());
            appointment.setFee(details.getFee());
        }

        // 🟢 ADD NEW SLOTS (the SAFE WAY)
        for (LocalTime time : details.getTimeSlots()) {
            TimeSlots ts = new TimeSlots();
            ts.setTimeslotId(ULIDGenerator.generate());
            ts.setTime(time);
            ts.setAppointmentDate(appointment);
            appointment.getTimeSlots().add(ts);  // add to existing collection
        }


        return appointmentDateRepository.save(appointment);
    }


    public List<DoctorResponseDTO> searchDoctorWithAvailability(String specialization, String cityName) {
        List<Object[]> rows =
                doctorRepository.searchAvailableDoctors(specialization, cityName);
        Map<String, DoctorResponseDTO> doctorMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String doctorId = (String) row[0];
            String spec = (String) row[1];
            String address = (String) row[2];
            String area = (String) row[3];
            String city = (String) row[4];
            String state = (String) row[5];
            String appointmentDateId = String.valueOf(row[6]);
            LocalDate date = (LocalDate) row[7];
            long fee = (long) row[8];
            String timeSlotId = String.valueOf(row[9]);
            String time = String.valueOf(row[10]);
            if (!doctorMap.containsKey(doctorId)) {
                String name = authClient.getDoctorNameById(doctorId);
//            String name ="doctorName"; //  getDoctorNameByIdFromAUTH-SERVICE  using feign client.
                doctorMap.putIfAbsent(
                        doctorId,
                        new DoctorResponseDTO(doctorId, name, spec, address, area, city, state)
                );
            }
            //  Schedule Date assigning
            DoctorResponseDTO doctorDTO = doctorMap.get(doctorId);
            DoctorScheduleDTO scheduleDTO = doctorDTO.getSchedules().stream()
                    .filter(s -> s.getAppointmentDateId().equals(appointmentDateId))
                    .findFirst()
                    .orElse(null);
            if (scheduleDTO == null) {
                scheduleDTO = new DoctorScheduleDTO(appointmentDateId, date, fee, new ArrayList<>());
                doctorDTO.addSchedule(scheduleDTO);
            }
            scheduleDTO.getTimeSlots().add(new TimeSlotDTO(timeSlotId, time));
        }
        return new ArrayList<>(doctorMap.values());
    }


    public DoctorBookingResponseDto verifyDoctorSchedule(String doctorId, String appointmentDateId, long fee, String timeSlotId) {
        long count = doctorRepository.isDoctorExistsBy(doctorId, appointmentDateId, fee, timeSlotId);
        boolean available = (count > 0);
        return DoctorBookingResponseDto.builder()
                .doctorId(doctorId)
                .fee(fee)
                .available(available)
                .build();
    }


}

