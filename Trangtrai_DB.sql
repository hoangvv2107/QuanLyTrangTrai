DROP DATABASE IF EXISTS Trangtrai;
CREATE DATABASE IF NOT EXISTS Trangtrai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE Trangtrai;

-- Bảng Nhân viên (Thực thể cha tổng quát)
CREATE TABLE NhanVien (
    MaNV VARCHAR(20) PRIMARY KEY,
    HoVaTen VARCHAR(100) NOT NULL,
    Sdt VARCHAR(15) NOT NULL,
    DiaChi VARCHAR(255),
    TrangThai VARCHAR(30) NOT NULL DEFAULT 'Đang làm việc' 
        CHECK (TrangThai IN ('Đang làm việc', 'Đã nghỉ việc'))
);

-- Bảng Nhà cung cấp
CREATE TABLE NhaCungCap (
    MaNCC VARCHAR(20) PRIMARY KEY,
    TenNCC VARCHAR(150) NOT NULL,
    DiaChi VARCHAR(255),
    Sdt VARCHAR(15) NOT NULL,
    LoaiNCC VARCHAR(50) NOT NULL CHECK (LoaiNCC IN ('Con giống', 'Thức ăn'))
);

-- Bảng Khách hàng
CREATE TABLE KhachHang (
    MaKH VARCHAR(20) PRIMARY KEY,
    TenKH VARCHAR(100) NOT NULL,
    Sdt VARCHAR(15) NOT NULL,
    DiaChi VARCHAR(255)
);

-- Bảng Thức ăn
CREATE TABLE ThucAn (
    MaThucAn VARCHAR(20) PRIMARY KEY,
    TenThucAn VARCHAR(100) NOT NULL,
    DonViTinh VARCHAR(20) NOT NULL
);

-- Bảng Chuồng
CREATE TABLE Chuong (
    MaChuong VARCHAR(20) PRIMARY KEY,
    TenChuong VARCHAR(100) NOT NULL,
    SucChua INT NOT NULL CHECK (SucChua > 0),
    ViTri VARCHAR(100)
);

-- Bảng Nhân viên Thú y (Kế thừa NhanVien)
CREATE TABLE NhanVienThuY (
    MaNV VARCHAR(20) PRIMARY KEY,
    ChuyenMon VARCHAR(100),
    ChungChiHanhNghe VARCHAR(100),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV) 
        ON UPDATE CASCADE 
        ON DELETE CASCADE
);

-- Bảng Nhân viên Chăm sóc (Kế thừa NhanVien)
CREATE TABLE NhanVienChamSoc (
    MaNV VARCHAR(20) PRIMARY KEY,
    SoNamKinhNghiem INT NOT NULL DEFAULT 0 CHECK (SoNamKinhNghiem >= 0),
    ViTriPhuTrach VARCHAR(100),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV) 
        ON UPDATE CASCADE 
        ON DELETE CASCADE
);

-- Bảng Phiếu nhập (Nhận khóa ngoại MaNCC)
CREATE TABLE PhieuNhap (
    MaPhieuNhap VARCHAR(20) PRIMARY KEY,
    NgayNhap DATE NOT NULL,
    TongTien DECIMAL(15, 2) NOT NULL DEFAULT 0 CHECK (TongTien >= 0),
    MaNCC VARCHAR(20) NOT NULL,
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

-- Bảng Phiếu xuất bán (Nhận khóa ngoại MaKH)
CREATE TABLE PhieuXuatBan (
    MaPhieuXuat VARCHAR(20) PRIMARY KEY,
    NgayXuat DATE NOT NULL,
    TongTien DECIMAL(15, 2) NOT NULL DEFAULT 0 CHECK (TongTien >= 0),
    MaKH VARCHAR(20) NOT NULL,
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

-- Bảng Động vật (Nhận khóa ngoại từ Chuong, PhieuNhap, PhieuXuatBan)
CREATE TABLE DongVat (
    MaDongVat VARCHAR(20) PRIMARY KEY,
    Loai VARCHAR(50) NOT NULL,
    GioiTinh VARCHAR(10) CHECK (GioiTinh IN ('Đực', 'Cái', 'Trống', 'Mái')),
    CanNang DECIMAL(6, 2) NOT NULL CHECK (CanNang > 0),
    TinhTrang VARCHAR(50),
    GhiChu TEXT,
    CanNangLucNhap DECIMAL(6, 2) NOT NULL CHECK (CanNangLucNhap > 0),
    GiaNhap DECIMAL(15, 2) NOT NULL CHECK (GiaNhap >= 0),
    CanNangLucXuat DECIMAL(6, 2) CHECK (CanNangLucXuat IS NULL OR CanNangLucXuat > 0),
    GiaBan DECIMAL(15, 2) CHECK (GiaBan IS NULL OR GiaBan >= 0),
    MaChuong VARCHAR(20) NOT NULL,
    MaPhieuNhap VARCHAR(20) NOT NULL,
    MaPhieuXuat VARCHAR(20) DEFAULT NULL,
    FOREIGN KEY (MaChuong) REFERENCES Chuong(MaChuong) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT,
    FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT,
    FOREIGN KEY (MaPhieuXuat) REFERENCES PhieuXuatBan(MaPhieuXuat) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

-- Bảng Hồ sơ bệnh án (Thực thể yếu theo DongVat, có bác sĩ thú y khám)
CREATE TABLE HoSoBenhAn (
    MaDongVat VARCHAR(20) NOT NULL,
    LanKham INT NOT NULL,
    NgayKham DATE NOT NULL,
    ChuanDoan TEXT NOT NULL,
    DonThuoc TEXT NULL,
    MaNV VARCHAR(20) NOT NULL,
    PRIMARY KEY (MaDongVat, LanKham),
    FOREIGN KEY (MaDongVat) REFERENCES DongVat(MaDongVat) 
        ON UPDATE CASCADE 
        ON DELETE CASCADE,
    FOREIGN KEY (MaNV) REFERENCES NhanVienThuY(MaNV) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

-- Bảng Chăm sóc (Quan hệ N-N giữa NhanVienChamSoc và DongVat theo ngày)
CREATE TABLE ChamSoc (
    MaNV VARCHAR(20) NOT NULL,
    MaDongVat VARCHAR(20) NOT NULL,
    NgayPhuTrach DATE NOT NULL,
    TinhTrangSucKhoe VARCHAR(100),
    PRIMARY KEY (MaNV, MaDongVat),
    FOREIGN KEY (MaNV) REFERENCES NhanVienChamSoc(MaNV) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT,
    FOREIGN KEY (MaDongVat) REFERENCES DongVat(MaDongVat) 
        ON UPDATE CASCADE 
        ON DELETE CASCADE
);

-- Bảng Gồm (Chi tiết nhập thức ăn - Quan hệ N-N giữa PhieuNhap và ThucAn)
CREATE TABLE Gom (
    MaPhieuNhap VARCHAR(20) NOT NULL,
    MaThucAn VARCHAR(20) NOT NULL,
    SoLuongNhap DECIMAL(10, 2) NOT NULL CHECK (SoLuongNhap > 0),
    DonGiaNhap DECIMAL(15, 2) NOT NULL CHECK (DonGiaNhap >= 0),
    PRIMARY KEY (MaPhieuNhap, MaThucAn),
    FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT,
    FOREIGN KEY (MaThucAn) REFERENCES ThucAn(MaThucAn) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

-- Dữ liệu mẫu phục vụ kiểm thử ứng dụng
INSERT INTO NhanVien (MaNV, HoVaTen, Sdt, DiaChi, TrangThai) VALUES
    ('NV001', 'Nguyễn Văn An', '0901000001', 'Hà Nội', 'Đang làm việc'),
    ('NV002', 'Trần Thị Bình', '0901000002', 'Hà Nội', 'Đang làm việc'),
    ('NV003', 'Lê Văn Cường', '0901000003', 'Hà Nam', 'Đang làm việc');

INSERT INTO NhanVienThuY (MaNV, ChuyenMon, ChungChiHanhNghe) VALUES
    ('NV002', 'Thú y gia súc', 'CC-TY-002');

INSERT INTO NhanVienChamSoc (MaNV, SoNamKinhNghiem, ViTriPhuTrach) VALUES
    ('NV003', 5, 'Khu chuồng A');

INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, Sdt, LoaiNCC) VALUES
    ('NCC001', 'Công ty Con giống Việt', 'Hà Nội', '0911000001', 'Con giống'),
    ('NCC002', 'Cửa hàng Thức ăn Xanh', 'Hưng Yên', '0911000002', 'Thức ăn');

INSERT INTO KhachHang (MaKH, TenKH, Sdt, DiaChi) VALUES
    ('KH001', 'Phạm Minh Đức', '0922000001', 'Hà Nội'),
    ('KH002', 'Đỗ Thu Hà', '0922000002', 'Bắc Ninh');

INSERT INTO ThucAn (MaThucAn, TenThucAn, DonViTinh) VALUES
    ('TA001', 'Cám gia súc tổng hợp', 'Kg'),
    ('TA002', 'Ngô hạt', 'Kg');

INSERT INTO Chuong (MaChuong, TenChuong, SucChua, ViTri) VALUES
    ('C001', 'Chuồng bò số 1', 20, 'Khu A'),
    ('C002', 'Chuồng lợn số 1', 30, 'Khu B'),
    ('C003', 'Chuồng gia cầm', 50, 'Khu C');

INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, TongTien, MaNCC) VALUES
    ('PN001', '2026-01-10', 45000000, 'NCC001'),
    ('PN002', '2026-02-15', 18000000, 'NCC001');

INSERT INTO PhieuXuatBan (MaPhieuXuat, NgayXuat, TongTien, MaKH) VALUES
    ('PX001', '2026-03-20', 12000000, 'KH001');

INSERT INTO DongVat
    (MaDongVat, Loai, GioiTinh, CanNang, TinhTrang, GhiChu,
     CanNangLucNhap, GiaNhap, CanNangLucXuat, GiaBan,
     MaChuong, MaPhieuNhap, MaPhieuXuat)
VALUES
    ('DV001', 'Bò', 'Đực', 320.50, 'Khỏe mạnh', 'Bò giống đực', 300.00, 25000000, NULL, NULL, 'C001', 'PN001', NULL),
    ('DV002', 'Bò', 'Cái', 280.00, 'Bình thường', 'Bò giống cái', 270.00, 20000000, NULL, NULL, 'C001', 'PN001', NULL),
    ('DV003', 'Lợn', 'Cái', 95.00, 'Đang theo dõi sức khỏe', 'Lợn nái', 80.00, 9000000, NULL, NULL, 'C002', 'PN002', NULL),
    ('DV004', 'Gà', 'Mái', 2.10, 'Khỏe mạnh', 'Gà đẻ trứng', 1.80, 150000, 2.00, 220000, 'C003', 'PN002', 'PX001');

INSERT INTO HoSoBenhAn (MaDongVat, LanKham, NgayKham, ChuanDoan, DonThuoc, MaNV) VALUES
    ('DV003', 1, '2026-02-20', 'Theo dõi tiêu hóa', 'Men tiêu hóa', 'NV002'),
    ('DV003', 2, '2026-03-05', 'Đã ổn định', 'Không kê đơn', 'NV002');

INSERT INTO ChamSoc (MaNV, MaDongVat, NgayPhuTrach, TinhTrangSucKhoe) VALUES
    ('NV003', 'DV001', '2026-03-01', 'Khỏe mạnh'),
    ('NV003', 'DV003', '2026-03-01', 'Đang theo dõi');

INSERT INTO Gom (MaPhieuNhap, MaThucAn, SoLuongNhap, DonGiaNhap) VALUES
    ('PN001', 'TA001', 500.00, 12000),
    ('PN002', 'TA002', 300.00, 8500);
