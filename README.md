# 🚀 aAntiGravity

aAntiGravity, Paper sunucularda yerçekiminden etkilenen blokların düşmesini kontrol etmek için geliştirilmiş hafif bir Minecraft eklentisidir. Kum, çakıl, örs, beton tozu ve benzeri blokların fizik davranışını `config.yml` dosyası üzerinden açıp kapatmanıza olanak tanır.

Eklenti, blok fizik olaylarını ve `FallingBlock` oluşumlarını kontrol ederek seçili blokların düşmesini engeller. Ayrıca oyun içi menü sayesinde yapılandırma dosyasını elle düzenlemeden blokları tek tek veya toplu şekilde yönetebilirsiniz.

---

## ✨ Özellikler

* 🧱 Yapılandırma üzerinden blok bazlı yerçekimi engelleme kontrolü
* 🎛️ Oyun içi blok ayarları menüsü
* 🔄 Yapılandırma yenileme komutu
* 🧭 `/ag` komut kısayolu desteği
* ✅ Tüm blokları tek tıkla açma veya kapatma
* 📄 Özelleştirilebilir mesajlar
* 🛡️ Yetki tabanlı komut erişimi
* ⚙️ Paper yaşam döngüsü komut sistemiyle modern komut kaydı

---

## 📜 Komutlar

| Komut | Kısayol | Açıklama |
| --- | --- | --- |
| `/aantigravity reload` | `/ag reload` | Yapılandırma dosyasını yeniden yükler. |
| `/aantigravity gui` | `/ag gui` | Blok ayarları menüsünü açar. Yalnızca oyuncular tarafından kullanılabilir. |

Komut eksik veya hatalı kullanıldığında eklenti kullanım bilgisini gösterir:

```text
/aantigravity reload | /aantigravity gui
```

---

## 🔑 Yetkiler

| Yetki | Açıklama | Varsayılan |
| --- | --- | --- |
| `aantigravity.reload` | Yapılandırma yenileme komutunu kullanma yetkisi verir. | `op` |
| `aantigravity.gui` | Blok ayarları menüsünü açma yetkisi verir. | `op` |

---

## ⚙️ Yapılandırma

Eklenti ayarları `src/main/resources/config.yml` içindeki varsayılan yapıdan oluşturulur.

| Ayar | Açıklama |
| --- | --- |
| `enabled` | Eklentinin blok düşmesini engelleme sistemini genel olarak açar veya kapatır. |
| `blocks` | Her blok için yerçekimi engelleme durumunu belirler. `true` olan bloklar düşmez, `false` olan bloklar normal davranır. |
| `messages` | Komut, yetki ve menü durum mesajlarını özelleştirir. Renk kodları için `&` formatı kullanılır. |

Varsayılan yapılandırmada kum, kırmızı kum, çakıl, örs türleri, beton tozları, ejderha yumurtası, sivri damlataş, şüpheli kum ve şüpheli çakıl gibi düşebilen bloklar tanımlıdır.

---

## 🎛️ Menü

`/aantigravity gui` komutu, blok ayarlarını oyun içinde yönetmek için 54 slotluk bir menü açar.

Menü üzerinden:

* 🧱 Blokların aktif/pasif durumunu tek tek değiştirebilirsiniz.
* ✅ Listedeki tüm blokları aynı anda aktif hale getirebilirsiniz.
* ❌ Listedeki tüm blokları aynı anda pasif hale getirebilirsiniz.
* ◀️ ▶️ Sayfalar arasında geçiş yapabilirsiniz.
* 📊 Toplam blok, aktif blok ve sayfa bilgilerini görebilirsiniz.

Aktif bloklar menüde parlak görünür ve artık düşmez. Pasif bloklar normal Minecraft fizik davranışını kullanır.

---

## 📦 Kurulum

1. Sunucuyu durdurun.
2. Derlenmiş `aAntiGravity-1.0.jar` dosyasını `plugins` klasörüne yerleştirin.
3. Sunucuyu başlatın.
4. Oluşan `config.yml` dosyasını ihtiyacınıza göre düzenleyin.
5. Ayarları uygulamak için sunucuyu yeniden başlatın veya `/aantigravity reload` komutunu kullanın.

Kaynak koddan derlemek için Maven kullanılabilir:

```bash
mvn clean package
```

Derlenen jar dosyası `target/` klasöründe oluşur.

---

## 🧩 Uyumluluk

* Paper `1.21.11`
* Java `21`
* Purpur gibi Paper tabanlı sunucularla uyumlu olması beklenir.

Bu eklenti Paper API ve Paper yaşam döngüsü komut sistemini kullandığı için doğrudan Spigot hedeflenerek yazılmamıştır.

---

## 📚 Bağımlılıklar

### Gerekli

* Paper API `1.21.11`

### Opsiyonel

* Ek eklenti entegrasyonu bulunmuyor.

---

## 🎯 Kullanım

Eklenti kurulduktan sonra varsayılan yapılandırmada aktif olan düşebilen bloklar artık normal şekilde düşmez. Örneğin kum, çakıl veya beton tozu gibi bloklar için fizik tetiklendiğinde eklenti ilgili olayı engeller.

Yönetici olarak `/aantigravity gui` komutunu kullanarak blok listesini oyun içinden açabilirsiniz. Menüde bir bloğa tıklamak, o bloğun yerçekimi engelleme durumunu değiştirir. Tüm blokları hızlıca açmak veya kapatmak için menünün alt kısmındaki toplu işlem butonları kullanılabilir.

Yapılandırma dosyasında değişiklik yaptıktan sonra `/aantigravity reload` komutu ile ayarları yeniden yükleyebilirsiniz. Eklenti, yapılandırmada geçersiz blok ismi varsa konsola uyarı yazar ve geçerli bloklarla çalışmaya devam eder.

---

## 📷 Ekran Görüntüleri

Henüz ekran görüntüsü eklenmemiştir.

> Menü ekran görüntüleri eklendiğinde bu bölüm güncellenebilir.

